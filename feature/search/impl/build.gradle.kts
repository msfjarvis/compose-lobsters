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
  api(libs.androidx.compose.foundation.layout)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.text)
  implementation(libs.androidx.compose.ui.unit)
  implementation(libs.eithernet)
  api(projects.feature.search.api)
  api(projects.core.common)
  api(projects.core.network.api)
  implementation(projects.feature.posts.api)
  api(projects.model)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material3)
  api(libs.androidx.compose.runtime)
  api(libs.androidx.compose.ui)
  api(libs.androidx.lifecycle.viewmodel)
  api(libs.androidx.paging.common)
  api(libs.androidx.paging.compose)
  api(libs.kotlinx.coroutines.core)
  api(libs.metrox.viewmodel)
  addTestDependencies(project)
}
