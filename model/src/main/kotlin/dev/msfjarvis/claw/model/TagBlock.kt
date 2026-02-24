/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.model

import dev.drewhamilton.poko.Poko

/**
 * Represents a blocked tag with an optional expiration date.
 *
 * @property tag The tag name to block
 * @property expirationMillis The expiration timestamp in milliseconds since epoch, or null for
 *   permanent blocks
 */
@Poko
class TagBlock(val tag: String, val expirationMillis: Long?) {
  val isPermanent: Boolean
    get() = expirationMillis == null

  val isExpired: Boolean
    get() = expirationMillis?.let { it < System.currentTimeMillis() } ?: false
}
