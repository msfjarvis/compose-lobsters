/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.model

import kotlin.time.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toInstant

internal fun String.parseInstantOrEpoch(): Instant =
  parseInstantOrNull() ?: Instant.fromEpochSeconds(0)

internal fun String.parseInstantOrNull(): Instant? {
  return when {
    isBlank() -> Instant.fromEpochSeconds(0)
    all(Char::isDigit) -> toLongOrNull()?.let(Instant::fromEpochSeconds)
    else -> parseIsoInstant() ?: parseLegacyLobstersDateTime()?.toInstant(TimeZone.UTC)
  }
}

private fun String.parseIsoInstant(): Instant? =
  try {
    Instant.parse(this)
  } catch (_: IllegalArgumentException) {
    null
  }

private fun String.parseLegacyLobstersDateTime(): LocalDateTime? =
  try {
    LEGACY_LOBSTERS_DATE_TIME_FORMAT.parse(this)
  } catch (_: IllegalArgumentException) {
    null
  }

@OptIn(FormatStringsInDatetimeFormats::class)
private val LEGACY_LOBSTERS_DATE_TIME_FORMAT = LocalDateTime.Format {
  byUnicodePattern("yyyy-MM-dd HH:mm:ss")
}
