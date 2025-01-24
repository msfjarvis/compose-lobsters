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
  alias(libs.plugins.anvil)
  alias(libs.plugins.whetstone)
  alias(libs.plugins.dependencyAnalysis)
}

android { namespace = "dev.msfjarvis.claw.api" }

anvil { generateDaggerFactories.set(true) }

dependencies {
  api(libs.dagger)
  api(libs.javax.inject)
  api(libs.okhttp.core)
  api(libs.retrofit)
  api(projects.model)

  implementation(libs.eithernet)
  implementation(libs.eithernet.integration.retrofit)
  implementation(libs.jsoup)

  testImplementation(libs.eithernet.test.fixtures)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.kotlinx.serialization.core)
  testImplementation(libs.kotlinx.serialization.json)
  addTestDependencies(project)
}
