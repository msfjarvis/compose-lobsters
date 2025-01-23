/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.glance

import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

fun FontWeight?.toGlance(): androidx.glance.text.FontWeight? {
  return when (this) {
    FontWeight.Normal -> androidx.glance.text.FontWeight.Normal
    FontWeight.Medium -> androidx.glance.text.FontWeight.Medium
    FontWeight.Bold -> androidx.glance.text.FontWeight.Bold
    else -> null
  }
}

fun FontStyle?.toGlance(): androidx.glance.text.FontStyle? {
  return when (this) {
    FontStyle.Normal -> androidx.glance.text.FontStyle.Normal
    FontStyle.Italic -> androidx.glance.text.FontStyle.Italic
    else -> null
  }
}
