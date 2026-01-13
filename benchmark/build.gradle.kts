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
  implementation(libs.androidx.benchmark.macro)
  implementation(libs.androidx.benchmark.macro.junit4)
  implementation(libs.androidx.monitor)
  implementation(libs.androidx.test.uiautomator)
  implementation(libs.kotlin.stdlib)
  implementation(libs.junit.legacy)

  runtimeOnly(libs.androidx.profileinstaller)
  runtimeOnly(libs.androidx.test.core)
  runtimeOnly(libs.androidx.test.rules)
  runtimeOnly(libs.androidx.test.runner)
  runtimeOnly(libs.androidx.tracing.perfetto)
  runtimeOnly(libs.androidx.tracing.perfetto.binary)
}
