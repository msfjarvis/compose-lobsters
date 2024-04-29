/*
 * Copyright © 2021-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
import dev.msfjarvis.claw.gradle.addTestDependencies

plugins {
  id("dev.msfjarvis.claw.android-library")
  id("dev.msfjarvis.claw.kotlin-android")
  alias(libs.plugins.anvil)
  alias(libs.plugins.whetstone)
}

android { namespace = "dev.msfjarvis.claw.database" }

anvil { generateDaggerFactories.set(true) }

dependencies {
  api(libs.dagger)
  api(libs.javax.inject)
  api(projects.database.core)
  implementation(libs.napier)
  implementation(libs.dagger)
  implementation(libs.sqldelight.androidDriver)
  implementation(libs.sqldelight.primitiveAdapters)
  implementation(libs.sqlite.android)

  testImplementation(libs.sqldelight.jvmDriver)
  addTestDependencies(project)
}
