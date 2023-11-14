/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("UnstableApiUsage")

plugins {
  id("dev.msfjarvis.claw.android-library")
  id("dev.msfjarvis.claw.kotlin-android")
  id("kotlin-parcelize")
  alias(libs.plugins.anvil)
}

android {
  buildFeatures {
    androidResources = true
    compose = true
  }
  composeOptions {
    useLiveLiterals = false
    kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
  }
  namespace = "dev.msfjarvis.claw.ui.search"
}

anvil { generateDaggerFactories.set(true) }

dependencies {
  api(projects.api)
  api(projects.database.core)
  api(projects.model)

  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.circuit.runtime)
  implementation(libs.circuit.runtime.presenter)
  implementation(libs.javax.inject)
  testImplementation(libs.circuit.test)
}
