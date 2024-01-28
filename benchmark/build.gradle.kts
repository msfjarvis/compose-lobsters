/*
 * Copyright © 2022-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("UnstableApiUsage")

plugins {
  id(libs.plugins.android.test.get().pluginId)
  id("dev.msfjarvis.claw.kotlin-android")
  alias(libs.plugins.baselineprofile)
}

android {
  namespace = "dev.msfjarvis.claw.benchmark"

  compileSdk = 34
  defaultConfig {
    minSdk = 28
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    create("benchmark") {
      isDebuggable = true
      signingConfig = signingConfigs["debug"]
      matchingFallbacks += "release"
    }
  }
  targetProjectPath = ":android"
}

baselineProfile {
  useConnectedDevices = true
  enableEmulatorDisplay = false
}

dependencies {
  implementation(libs.androidx.benchmark.macro.junit4)
  implementation(libs.androidx.profileinstaller)
  implementation(libs.androidx.test.core)
  implementation(libs.androidx.test.espresso.core)
  implementation(libs.androidx.test.ext.junit)
  implementation(libs.androidx.test.rules)
  implementation(libs.androidx.test.runner)
  implementation(libs.androidx.test.uiautomator)
}
