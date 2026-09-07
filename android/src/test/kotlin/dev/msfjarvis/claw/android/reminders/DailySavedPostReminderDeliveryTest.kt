/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.reminders

import androidx.work.ListenableWorker
import com.google.common.truth.Truth.assertThat
import dev.msfjarvis.claw.android.viewmodel.DailySavedPostNotificationRepository
import dev.msfjarvis.claw.database.local.DailySavedPostNotification
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import org.junit.jupiter.api.Test

class DailySavedPostReminderDeliveryTest {

  @Test
  fun `disabled reminder does not select or notify`() = runTest {
    val repository = FakeRepository(selection = selection())
    val notifier = FakeNotifier()

    val result = delivery(repository, FakeSettings(enabled = false), notifier).deliver()

    assertSuccess(result)
    assertThat(repository.selectionRequests).isEmpty()
    assertThat(notifier.notifications).isEmpty()
  }

  @Test
  fun `unavailable notifications do not consume a selection`() = runTest {
    val repository = FakeRepository(selection = selection())

    val result = delivery(repository, FakeSettings(), FakeNotifier(available = false)).deliver()

    assertSuccess(result)
    assertThat(repository.selectionRequests).isEmpty()
  }

  @Test
  fun `empty storage succeeds without notifying`() = runTest {
    val repository = FakeRepository(selection = null)
    val notifier = FakeNotifier()

    val result = delivery(repository, FakeSettings(), notifier).deliver()

    assertSuccess(result)
    assertThat(repository.selectionRequests).containsExactly("2026-09-05")
    assertThat(notifier.notifications).isEmpty()
  }

  @Test
  fun `first delivery marks the selected date after notifying`() = runTest {
    val repository = FakeRepository(selection = selection())
    val notifier = FakeNotifier()

    val result = delivery(repository, FakeSettings(), notifier).deliver()

    assertSuccess(result)
    assertThat(notifier.notifications.map { it.first }).containsExactly("2026-09-05")
    assertThat(repository.markedDelivered).containsExactly("2026-09-05" to 1_788_609_600_000L)
  }

  @Test
  fun `already delivered selection is a same-day no-op`() = runTest {
    val repository = FakeRepository(selection = selection(deliveredAtEpochMillis = 1L))
    val notifier = FakeNotifier()

    val result = delivery(repository, FakeSettings(), notifier).deliver()

    assertSuccess(result)
    assertThat(notifier.notifications).isEmpty()
    assertThat(repository.markedDelivered).isEmpty()
  }

  @Test
  fun `existing undelivered selection is delivered`() = runTest {
    val repository = FakeRepository(selection = selection())
    val notifier = FakeNotifier()

    val result = delivery(repository, FakeSettings(), notifier).deliver()

    assertSuccess(result)
    assertThat(notifier.notifications).hasSize(1)
    assertThat(repository.markedDelivered).hasSize(1)
  }

  @Test
  fun `notification becoming unavailable preserves an existing selection`() = runTest {
    val repository = FakeRepository(selection = selection())

    val result =
      delivery(
          repository,
          FakeSettings(),
          FakeNotifier(deliveryResult = Unavailable),
        )
        .deliver()

    assertSuccess(result)
    assertThat(repository.markedDelivered).isEmpty()
  }

  @Test
  fun `transient notification failure retries without marking delivered`() = runTest {
    val repository = FakeRepository(selection = selection())

    val result =
      delivery(
          repository,
          FakeSettings(),
          FakeNotifier(deliveryResult = Retry),
        )
        .deliver()

    assertRetry(result)
    assertThat(repository.markedDelivered).isEmpty()
  }

  @Test
  fun `worker date is captured in its injected zone`() = runTest {
    val repository = FakeRepository(selection = selection())
    val notifier = FakeNotifier()

    val result =
      delivery(
          repository,
          FakeSettings(),
          notifier,
          clock = Clock.fixed(Instant.parse("2026-09-05T00:30:00Z")),
          timeZone = TimeZone.of("America/Los_Angeles"),
        )
        .deliver()

    assertSuccess(result)
    assertThat(repository.selectionRequests).containsExactly("2026-09-04")
    assertThat(notifier.notifications.map { it.first }).containsExactly("2026-09-04")
  }

  private fun delivery(
    repository: DailySavedPostNotificationRepository,
    settings: DailySavedPostReminderSettings,
    notifier: DailySavedPostReminderNotifier,
    clock: Clock = Clock.fixed(Instant.parse("2026-09-05T12:00:00Z")),
    timeZone: TimeZone = UTC,
  ) = DailySavedPostReminderDelivery(repository, settings, notifier, clock, timeZone)

  private fun selection(deliveredAtEpochMillis: Long? = null) =
    DailySavedPostNotification(
      localDate = "2026-09-05",
      shortId = "post",
      title = "Post title",
      url = "https://example.test/post",
      selectedAtEpochMillis = 1L,
      deliveredAtEpochMillis = deliveredAtEpochMillis,
    )

  // Source - https://stackoverflow.com/a/75419837
  // Posted by Endzeit
  // Retrieved 2026-09-07, License - CC BY-SA 4.0
  private class FixedClock(private val fixedInstant: Instant) : Clock {
    override fun now(): Instant = fixedInstant
  }

  private fun Clock.Companion.fixed(fixedInstant: Instant): Clock = FixedClock(fixedInstant)

  private fun assertSuccess(result: ListenableWorker.Result) {
    assertThat(result).isEqualTo(ListenableWorker.Result.success())
  }

  private fun assertRetry(result: ListenableWorker.Result) {
    assertThat(result).isEqualTo(ListenableWorker.Result.retry())
  }

  private class FakeSettings(enabled: Boolean = true) : DailySavedPostReminderSettings {
    override val isEnabled: StateFlow<Boolean> = MutableStateFlow(enabled)

    override fun setEnabled(enabled: Boolean) = Unit
  }

  private class FakeRepository(private val selection: DailySavedPostNotification?) :
    DailySavedPostNotificationRepository {
    val selectionRequests = mutableListOf<String>()
    val markedDelivered = mutableListOf<Pair<String, Long>>()

    override suspend fun getOrCreateSelection(
      localDate: String,
      selectedAtEpochMillis: Long,
    ): DailySavedPostNotification? {
      selectionRequests += localDate
      return selection
    }

    override suspend fun markDelivered(localDate: String, deliveredAtEpochMillis: Long) {
      markedDelivered += localDate to deliveredAtEpochMillis
    }
  }

  private class FakeNotifier(
    private val available: Boolean = true,
    private val deliveryResult: NotificationDeliveryResult = Delivered,
  ) : DailySavedPostReminderNotifier {
    val notifications = mutableListOf<Pair<String, DailySavedPostNotification>>()

    override fun isAvailable() = available

    override fun notify(
      localDate: String,
      selection: DailySavedPostNotification,
    ): NotificationDeliveryResult {
      notifications += localDate to selection
      return deliveryResult
    }
  }
}
