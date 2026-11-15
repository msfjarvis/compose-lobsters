/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
import dev.msfjarvis.claw.gradle.addTestDependencies

plugins {
  id("dev.msfjarvis.claw.android-library")
  id("dev.msfjarvis.claw.kotlin-android")
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.kotlin.composeCompiler)
  alias(libs.plugins.metro)
}

android {
  namespace = "dev.msfjarvis.claw.feature.profile.impl"
  androidResources.enable = true
}

dependencies {
  api(projects.core.common)
  api(projects.core.navigation.api)
  api(projects.core.network.api)
  api(libs.androidx.compose.foundation.layout)
  api(libs.androidx.compose.runtime)
  api(libs.androidx.compose.ui)
  api(libs.androidx.lifecycle.viewmodel)
  api(libs.androidx.navigation3.runtime)
  api(libs.kotlinx.coroutines.core)
  api(libs.metrox.viewmodel)

  implementation(projects.core.ui)
  implementation(projects.feature.profile.api)
  implementation(projects.model)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.text)
  implementation(libs.androidx.compose.ui.unit)
  implementation(libs.androidx.lifecycle.compose)
  implementation(libs.eithernet)
  implementation(libs.kotlinResult)
  implementation(libs.kotlinResult.coroutines)
  implementation(libs.metrox.viewmodel.compose)

  compileOnly(libs.androidx.compose.ui.tooling.preview)

  addTestDependencies(project)
}
