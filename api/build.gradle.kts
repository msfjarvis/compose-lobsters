/*
 * Copyright © 2021-2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

plugins {
  id("dev.msfjarvis.claw.kotlin-android")
  id("dev.msfjarvis.claw.android-library")
  alias(libs.plugins.anvil)
  alias(libs.plugins.whetstone)
}

android { namespace = "dev.msfjarvis.claw.api" }

anvil { generateDaggerFactories.set(true) }

dependencies {
  api(projects.model)
  api(libs.retrofit)
  api(libs.eithernet)
  implementation(libs.dagger)
  implementation(libs.javax.inject)
  testImplementation(testFixtures(libs.eithernet))
  testImplementation(libs.kotest.assertions.core)
  testImplementation(libs.kotest.runner.junit5)
  testImplementation(libs.kotlinx.serialization.json)
  testImplementation(libs.retrofit.kotlinxSerializationConverter)
}
