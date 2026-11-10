/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import dev.msfjarvis.claw.core.database.DailySavedPostLedgerEntry
import dev.msfjarvis.claw.core.database.DailySavedPostLedgerStorage
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

interface DailySavedPostNotificationRepository {
  suspend fun getOrCreateSelection(
    localDate: String,
    selectedAtEpochMillis: Long,
  ): DailySavedPostLedgerEntry?

  suspend fun markDelivered(localDate: String, deliveredAtEpochMillis: Long)

  suspend fun deleteDeliveryForDate(localDate: String)
}

@Inject
@ContributesBinding(AppScope::class, binding = binding<DailySavedPostNotificationRepository>())
class SqlDelightDailySavedPostNotificationRepository(
  private val storage: DailySavedPostLedgerStorage
) : DailySavedPostNotificationRepository {
  override suspend fun getOrCreateSelection(
    localDate: String,
    selectedAtEpochMillis: Long,
  ): DailySavedPostLedgerEntry? = storage.getOrCreateSelection(localDate, selectedAtEpochMillis)

  override suspend fun markDelivered(localDate: String, deliveredAtEpochMillis: Long) {
    storage.markDelivered(localDate, deliveredAtEpochMillis)
  }

  override suspend fun deleteDeliveryForDate(localDate: String) {
    storage.deleteDeliveryForDate(localDate)
  }
}
