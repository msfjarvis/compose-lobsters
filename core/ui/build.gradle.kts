/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("UnstableApiUsage")

import dev.msfjarvis.claw.gradle.addTestDependencies

plugins {
  id("dev.msfjarvis.claw.android-library")
  id("dev.msfjarvis.claw.kotlin-android")
  id("dev.msfjarvis.claw.android-screenshot")
  alias(libs.plugins.kotlin.composeCompiler)
  alias(libs.plugins.dependencyAnalysis)
}

android {
  namespace = "dev.msfjarvis.claw.core.ui"
  buildFeatures.buildConfig = true
}

dependencies {
  api(libs.androidx.compose.foundation)
  api(libs.androidx.compose.foundation.layout)
  api(libs.androidx.compose.material3)
  api(libs.androidx.compose.runtime)
  api(libs.androidx.compose.ui)
  api(libs.androidx.compose.ui.graphics)
  api(libs.androidx.compose.ui.text)
  api(libs.androidx.compose.ui.unit)
  api(libs.kotlinx.collections.immutable)
  api(projects.model)

  implementation(libs.androidx.compose.animation)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.core)
  implementation(libs.coil3.compose)
  implementation(projects.htmlRenderer)

  compileOnly(libs.androidx.compose.ui.tooling.preview)

  addTestDependencies(project)
}
