/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
plugins {
  id("dev.msfjarvis.claw.android-library")
  id("dev.msfjarvis.claw.kotlin-android")
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.metro)
}

android { namespace = "dev.msfjarvis.claw.work.api" }

dependencies {
  api(libs.androidx.work.runtime)
}
