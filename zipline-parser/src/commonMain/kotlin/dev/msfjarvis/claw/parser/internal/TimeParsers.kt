/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.parser.internal

import kotlin.time.Instant

internal fun normalizeCreatedAt(value: String): String {
  return when {
    value.isBlank() -> ""
    value.all(Char::isDigit) -> Instant.fromEpochSeconds(value.toLong()).toString()
    runCatching { Instant.parse(value) }.isSuccess -> value
    else -> value
  }
}
