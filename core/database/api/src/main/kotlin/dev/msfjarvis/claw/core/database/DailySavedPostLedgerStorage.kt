/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.database

/** Domain-facing persisted selection and delivery state for a daily saved-post reminder. */
data class DailySavedPostLedgerEntry(
  val localDate: String,
  val shortId: String,
  val title: String,
  val url: String,
  val selectedAtEpochMillis: Long,
  val deliveredAtEpochMillis: Long?,
)

/** Storage operations for the reminder ledger; reminder selection policy remains feature-owned. */
interface DailySavedPostLedgerStorage {
  suspend fun getOrCreateSelection(
    localDate: String,
    selectedAtEpochMillis: Long,
  ): DailySavedPostLedgerEntry?

  suspend fun markDelivered(localDate: String, deliveredAtEpochMillis: Long)

  suspend fun deleteDeliveryForDate(localDate: String)
}
