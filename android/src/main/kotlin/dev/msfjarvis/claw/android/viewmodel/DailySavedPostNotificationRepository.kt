/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import dev.msfjarvis.claw.core.coroutines.DatabaseWriteDispatcher
import dev.msfjarvis.claw.database.local.DailySavedPostNotification
import dev.msfjarvis.claw.database.local.DailySavedPostNotificationQueries
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface DailySavedPostNotificationRepository {
  suspend fun getOrCreateSelection(
    localDate: String,
    selectedAtEpochMillis: Long,
  ): DailySavedPostNotification?

  suspend fun markDelivered(localDate: String, deliveredAtEpochMillis: Long)

  suspend fun deleteDeliveryForDate(localDate: String)
}

@Inject
@ContributesBinding(AppScope::class, binding = binding<DailySavedPostNotificationRepository>())
class SqlDelightDailySavedPostNotificationRepository(
  private val notificationQueries: DailySavedPostNotificationQueries,
  @param:DatabaseWriteDispatcher private val writeDispatcher: CoroutineDispatcher,
) : DailySavedPostNotificationRepository {
  override suspend fun getOrCreateSelection(
    localDate: String,
    selectedAtEpochMillis: Long,
  ): DailySavedPostNotification? =
    withContext(writeDispatcher) {
      var selection: DailySavedPostNotification? = null
      notificationQueries.transaction {
        notificationQueries.createSelectionIfAbsent(localDate, selectedAtEpochMillis)
        selection = notificationQueries.selectionForDate(localDate).executeAsOneOrNull()
      }
      selection
    }

  override suspend fun markDelivered(localDate: String, deliveredAtEpochMillis: Long) {
    withContext(writeDispatcher) {
      notificationQueries.markDelivered(
        deliveredAtEpochMillis = deliveredAtEpochMillis,
        localDate = localDate,
      )
    }
  }

  override suspend fun deleteDeliveryForDate(localDate: String) {
    withContext(writeDispatcher) { notificationQueries.deleteDeliveryForDate(localDate) }
  }
}
