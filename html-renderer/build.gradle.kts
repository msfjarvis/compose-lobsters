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
  alias(libs.plugins.kotlin.composeCompiler)
  alias(libs.plugins.screenshot)
}

android {
  namespace = "dev.msfjarvis.claw.htmlrenderer"
  androidResources.enable = true
  buildFeatures { compose = true }
  // JUnit's test artifacts each bundle the same license resources.
  packaging.resources.merges += setOf("META-INF/LICENSE.md", "META-INF/LICENSE-notice.md")
  experimentalProperties["android.experimental.enableScreenshotTest"] = true
  defaultConfig { testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
}

kotlin {
  explicitApi()
}

dependencies {
  implementation(libs.ksoup)
  implementation(libs.kotlinx.collections.immutable)
  implementation(libs.androidx.compose.material3)
  api(libs.androidx.compose.runtime)
  api(libs.androidx.compose.ui)
  api(libs.androidx.compose.ui.text)
  api(libs.androidx.compose.ui.unit)
  api(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.foundation.layout)
  compileOnly(libs.androidx.compose.ui.tooling.preview)
  screenshotTestImplementation(libs.screenshot.validation.api)
  screenshotTestImplementation(libs.androidx.compose.ui.tooling)
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.junit.legacy)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  addTestDependencies(project)
}
