/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("UnstableApiUsage")

import com.android.compose.screenshot.tasks.PreviewScreenshotValidationTask
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

// Emoji rendering varies slightly between Linux and macOS.
tasks.withType<PreviewScreenshotValidationTask>().configureEach {
  testEngineInput.threshold.set(0.0004f)
}

dependencies {
  api(projects.model)
  api(libs.androidx.compose.animation)
  api(libs.androidx.compose.foundation)
  api(libs.androidx.compose.foundation.layout)
  api(libs.androidx.compose.material3)
  api(libs.androidx.compose.runtime)
  api(libs.androidx.compose.ui)
  api(libs.androidx.compose.ui.graphics)
  api(libs.androidx.compose.ui.text)
  api(libs.androidx.compose.ui.unit)
  api(libs.kotlinx.collections.immutable)

  implementation(projects.htmlRenderer)
  implementation(libs.androidx.compose.animation.core)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.runtime.annotation)
  implementation(libs.androidx.core)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.navigation3.ui)
  implementation(libs.coil3.compose)
  implementation(libs.kotlinx.coroutines.core)

  compileOnly(libs.androidx.compose.ui.tooling.preview)

  androidTestImplementation(libs.androidx.compose.ui.geometry)
  androidTestImplementation(libs.androidx.compose.ui.test)

  addTestDependencies(project)
}
