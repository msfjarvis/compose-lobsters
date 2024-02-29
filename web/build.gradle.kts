/*
 * Copyright © 2023-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("UnstableApiUsage")

plugins {
  id("dev.msfjarvis.claw.android-library")
  id("dev.msfjarvis.claw.kotlin-android")
}

android {
  namespace = "com.google.accompanist.web"
  buildFeatures.compose = true
  composeOptions.kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
  kotlin.explicitApi()
  // Don't quite care
  lint.disable += "DeprecatedCall"
}

dependencies {
  implementation(projects.core)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui.util)
  implementation(libs.androidx.lifecycle.runtime)
  implementation(libs.androidx.activity.compose)
  implementation(libs.kotlinx.collections.immutable)
}
