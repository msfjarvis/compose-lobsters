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
  id("dev.msfjarvis.claw.android-screenshot")
}

android {
  namespace = "dev.msfjarvis.claw.htmlrenderer"
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
  addTestDependencies(project)
}
