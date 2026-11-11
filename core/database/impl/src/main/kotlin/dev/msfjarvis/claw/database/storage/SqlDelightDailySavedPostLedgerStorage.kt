/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.database.storage

import dev.msfjarvis.claw.core.coroutines.DatabaseReadDispatcher
import dev.msfjarvis.claw.core.coroutines.DatabaseWriteDispatcher
import dev.msfjarvis.claw.core.database.DailySavedPostLedgerEntry
import dev.msfjarvis.claw.core.database.DailySavedPostLedgerStorage
import dev.msfjarvis.claw.database.local.DailySavedPostNotificationQueries
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class SqlDelightDailySavedPostLedgerStorage(
  private val queries: DailySavedPostNotificationQueries,
  @param:DatabaseReadDispatcher private val readDispatcher: CoroutineDispatcher,
  @param:DatabaseWriteDispatcher private val writeDispatcher: CoroutineDispatcher,
) : DailySavedPostLedgerStorage {
  override suspend fun getOrCreateSelection(
    localDate: String,
    selectedAtEpochMillis: Long,
  ): DailySavedPostLedgerEntry? =
    withContext(writeDispatcher) {
      queries.transactionWithResult {
        queries.createSelectionIfAbsent(localDate, selectedAtEpochMillis)
        queries.selectionForDate(localDate).executeAsOneOrNull()?.let { row ->
          DailySavedPostLedgerEntry(
            localDate = row.localDate,
            shortId = row.shortId,
            title = row.title,
            url = row.url,
            selectedAtEpochMillis = row.selectedAtEpochMillis,
            deliveredAtEpochMillis = row.deliveredAtEpochMillis,
          )
        }
      }
    }

  override suspend fun markDelivered(localDate: String, deliveredAtEpochMillis: Long) {
    withContext(writeDispatcher) {
      queries.markDelivered(
        deliveredAtEpochMillis = deliveredAtEpochMillis,
        localDate = localDate,
      )
    }
  }

  override suspend fun deleteDeliveryForDate(localDate: String) {
    withContext(writeDispatcher) { queries.deleteDeliveryForDate(localDate) }
  }
}
