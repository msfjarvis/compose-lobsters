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
  api(projects.feature.profile.api)
  implementation(projects.feature.discussion.api)
  implementation(projects.feature.posts.api)
  implementation(projects.core.common)
  implementation(projects.core.network.api)
  implementation(projects.core.navigation.api)
  implementation(projects.core.ui)
  implementation(projects.model)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.lifecycle.viewmodel)
  implementation(libs.kotlinResult)
  implementation(libs.kotlinResult.coroutines)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.metrox.viewmodel)
  implementation(libs.metrox.viewmodel.compose)
  compileOnly(libs.androidx.compose.ui.tooling.preview)
  addTestDependencies(project)
}
