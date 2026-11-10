/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
plugins {
  id("dev.msfjarvis.claw.android-library")
  id("dev.msfjarvis.claw.kotlin-android")
  id("kotlin-parcelize")
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.kotlin.serialization)
}

android { namespace = "dev.msfjarvis.claw.feature.tags.api" }

dependencies {
  api(projects.model)
  implementation(projects.core.navigation.api)
  api(libs.androidx.navigation3.runtime)
  api(libs.kotlinx.coroutines.core)
  api(libs.kotlinx.serialization.core)
}
