/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
plugins {
  id("dev.msfjarvis.claw.spotless")
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.spotless) apply false
  alias(libs.plugins.screenshot) apply false
  alias(libs.plugins.android.test) apply false
  alias(libs.plugins.dependencyAnalysis)
}
