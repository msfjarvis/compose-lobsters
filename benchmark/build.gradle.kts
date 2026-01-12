/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("UnstableApiUsage")

plugins {
  alias(libs.plugins.android.test)
  id("dev.msfjarvis.claw.kotlin-android")
  alias(libs.plugins.baselineprofile)
  alias(libs.plugins.dependencyAnalysis)
}

android {
  namespace = "dev.msfjarvis.claw.benchmark"

  compileSdk = 36
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
  testOptions {
    managedDevices {
      localDevices.create("pixel6Api31") {
        device = "Pixel 6"
        apiLevel = 31
        systemImageSource = "aosp-atd"
      }
    }
  }
  targetProjectPath = ":android"
}

baselineProfile {
  managedDevices += "pixel6Api31"
  useConnectedDevices = false
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

  implementation(libs.androidx.tracing.perfetto)
  implementation(libs.androidx.tracing.perfetto.binary)
}
