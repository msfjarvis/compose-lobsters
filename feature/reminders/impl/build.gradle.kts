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
  alias(libs.plugins.metro)
}

android {
  namespace = "dev.msfjarvis.claw.feature.reminders.impl"
  androidResources.enable = true
  buildFeatures.buildConfig = true
}

dependencies {
  implementation(projects.feature.reminders.api)
  implementation(projects.feature.posts.api)
  implementation(projects.core.common)
  implementation(projects.core.database.api)
  implementation(projects.core.navigation.api)
  implementation(projects.core.work.api)
  implementation(projects.model)
  implementation(libs.androidx.core)
  implementation(libs.androidx.work.runtime)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.kotlinx.datetime)
  implementation(libs.metrox.android)
  testImplementation(libs.kotlinx.coroutines.test)
  addTestDependencies(project)
}
