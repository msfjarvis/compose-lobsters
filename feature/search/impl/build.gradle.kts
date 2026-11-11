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
  namespace = "dev.msfjarvis.claw.feature.search.impl"
  androidResources.enable = true
}

dependencies {
  api(projects.feature.search.api)
  implementation(projects.core.common)
  implementation(projects.core.network.api)
  implementation(projects.feature.posts.api)
  implementation(projects.model)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.lifecycle.viewmodel)
  implementation(libs.androidx.paging.common)
  implementation(libs.androidx.paging.compose)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.metrox.viewmodel)
  addTestDependencies(project)
}
