/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.model

import kotlin.time.Clock

class TagBlock(val tag: String, val expirationMillis: Long?) {
  val isPermanent: Boolean
    get() = expirationMillis == null

  val isExpired: Boolean
    get() = expirationMillis?.let { it < Clock.System.now().toEpochMilliseconds() } ?: false
}
