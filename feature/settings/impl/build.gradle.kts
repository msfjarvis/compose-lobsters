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
  namespace = "dev.msfjarvis.claw.feature.settings.impl"
  buildFeatures.compose = true
  androidResources.enable = true
}

dependencies {
  api(projects.feature.settings.api)
  implementation(projects.core.navigation.api)
  implementation(projects.feature.auth.api)
  implementation(projects.feature.posts.api)
  implementation(projects.feature.tags.api)
  implementation(projects.feature.reminders.api)
  implementation(projects.core.ui)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.lifecycle.viewmodel)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.kotlinx.datetime)
  implementation(libs.metrox.viewmodel)
  testImplementation(libs.kotlinx.coroutines.test)
  addTestDependencies(project)
}
