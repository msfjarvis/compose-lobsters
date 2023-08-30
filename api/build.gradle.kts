/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
import dev.msfjarvis.claw.gradle.addTestDependencies

plugins {
  id("dev.msfjarvis.claw.android-library")
  id("dev.msfjarvis.claw.kotlin-android")
  alias(libs.plugins.anvil)
}

android { namespace = "dev.msfjarvis.claw.api" }

anvil { generateDaggerFactories.set(true) }

dependencies {
  anvil(libs.whetstone.compiler)
  api(libs.eithernet)
  api(libs.retrofit)
  api(projects.model)

  implementation(libs.dagger)
  implementation(libs.javax.inject)
  implementation(libs.jsoup)
  implementation(libs.whetstone)

  testImplementation(testFixtures(libs.eithernet))
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.kotlinx.serialization.json)
  addTestDependencies(project)
}
