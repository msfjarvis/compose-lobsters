/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.settings.impl

/** App-owned values required to render settings without depending on application entry points. */
data class SettingsAppConfig(
  val versionName: String,
  val versionCode: Int,
  val recordMetric: (String) -> Unit,
)
