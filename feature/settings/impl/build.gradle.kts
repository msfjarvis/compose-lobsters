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
  id("dev.msfjarvis.claw.android-screenshot")
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
  api(projects.core.navigation.api)
  api(projects.feature.auth.api)
  api(projects.feature.posts.api)
  api(projects.feature.reminders.api)
  api(projects.feature.tags.api)
  api(libs.androidx.compose.foundation.layout)
  api(libs.androidx.compose.material3)
  api(libs.androidx.compose.runtime)
  api(libs.androidx.compose.ui)
  api(libs.androidx.lifecycle.viewmodel)
  api(libs.kotlinx.coroutines.core)
  api(libs.kotlinx.datetime)
  api(libs.metrox.viewmodel)

  implementation(projects.core.ui)
  implementation(projects.feature.settings.api)
  implementation(libs.androidx.activity)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.text)
  implementation(libs.androidx.compose.ui.unit)
  implementation(libs.androidx.navigation3.runtime)

  testImplementation(projects.model)
  testImplementation(libs.kotlinx.coroutines.test)

  addTestDependencies(project)
}
