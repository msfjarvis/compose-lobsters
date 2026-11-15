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
  api(projects.core.database.api)
  api(projects.core.work.api)
  api(projects.feature.reminders.api)
  api(libs.androidx.work.runtime)
  api(libs.kotlinx.coroutines.core)
  api(libs.kotlinx.datetime)

  implementation(libs.androidx.core)

  testImplementation(libs.kotlinx.coroutines.test)

  addTestDependencies(project)
}
