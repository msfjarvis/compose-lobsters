/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
plugins {
  id("dev.msfjarvis.claw.android-library")
  id("dev.msfjarvis.claw.kotlin-android")
  alias(libs.plugins.kotlin.composeCompiler)
  alias(libs.plugins.dependencyAnalysis)
}

android { namespace = "dev.msfjarvis.claw.navigation.impl" }

dependencies {
  api(projects.core.navigation.api)
  api(libs.androidx.compose.runtime)
  api(libs.androidx.compose.ui)
  api(libs.androidx.navigation3.runtime)
  api(libs.kotlinx.collections.immutable)

  implementation(projects.core.ui)
  implementation(libs.androidx.compose.animation)
  implementation(libs.androidx.navigation3.ui)
}
