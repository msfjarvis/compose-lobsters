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
  namespace = "dev.msfjarvis.claw.feature.posts.impl"
  androidResources.enable = true
}

dependencies {
  implementation(projects.feature.posts.api)
  implementation(projects.core.common)
  implementation(projects.core.database.api)
  implementation(projects.core.network.api)
  implementation(projects.core.navigation.api)
  implementation(projects.core.ui)
  implementation(projects.core.work.api)
  implementation(projects.model)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.material3.navigation3)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.paging.common)
  implementation(libs.androidx.paging.compose)
  implementation(libs.androidx.lifecycle.viewmodel)
  implementation(libs.androidx.work.runtime)
  implementation(libs.kotlinx.collections.immutable)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.metrox.android)
  implementation(libs.metrox.viewmodel)
  implementation(libs.swipe)
  implementation(libs.unfurl)
  testImplementation(libs.kotlinx.coroutines.test)
  addTestDependencies(project)
}
