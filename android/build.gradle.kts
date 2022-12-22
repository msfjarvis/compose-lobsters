/*
 * Copyright © 2021-2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

plugins {
  id("dev.msfjarvis.claw.android-application")
  id("dev.msfjarvis.claw.rename-artifacts")
  id("dev.msfjarvis.claw.kotlin-android")
  id("dev.msfjarvis.claw.kotlin-kapt")
  id("dev.msfjarvis.claw.versioning-plugin")
  alias(libs.plugins.anvil)
  alias(libs.plugins.whetstone)
}

whetstone {
  addOns {
    compose.set(true)
    workManager.set(true)
  }
}

android {
  namespace = "dev.msfjarvis.claw.android"
  defaultConfig { applicationId = "dev.msfjarvis.claw.android" }
  buildFeatures { compose = true }
  composeOptions {
    useLiveLiterals = false
    kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
  }
  buildTypes {
    create("benchmark") {
      signingConfig = signingConfigs["debug"]
      matchingFallbacks += "release"
      isDebuggable = false
      proguardFile("benchmark-rules.pro")
    }
  }
}

dependencies {
  kapt(libs.dagger.compiler)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.dagger)
  implementation(projects.api)
  implementation(projects.common)
  implementation(projects.core)
  implementation(projects.coroutineUtils)
  implementation(projects.database)
  implementation(projects.metadataExtractor)
  implementation(projects.model)
  implementation(libs.accompanist.sysuicontroller)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.material)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.core.splashscreen)
  implementation(libs.androidx.lifecycle.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.paging.compose)
  implementation(libs.androidx.profileinstaller)
  implementation(libs.androidx.work.runtime.ktx)
  implementation(libs.coil)
  implementation(libs.copydown)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.sqldelight.extensions.coroutines)
}
