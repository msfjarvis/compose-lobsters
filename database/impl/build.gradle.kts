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

android { namespace = "dev.msfjarvis.claw.database" }

dependencies {
  api(projects.database.core)
  implementation(libs.napier)
  implementation(libs.sqldelight.androidDriver)
  implementation(libs.sqldelight.primitiveAdapters)
  implementation(libs.sqlite.android)

  testImplementation(libs.sqldelight.jvmDriver)
  addTestDependencies(project)
}
