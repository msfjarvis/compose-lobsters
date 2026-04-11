/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("UnstableApiUsage")

plugins {
  alias(libs.plugins.android.test)
  id("dev.msfjarvis.claw.android-common")
  id("dev.msfjarvis.claw.kotlin-android")
  alias(libs.plugins.baselineprofile)
  alias(libs.plugins.dependencyAnalysis)
}

android {
  namespace = "dev.msfjarvis.claw.benchmark"

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
  testOptions {
    managedDevices {
      localDevices {
        create("pixel9api36") {
          device = "Pixel 9"
          apiLevel = 36
          systemImageSource = "aosp"
        }
      }
    }
  }
}

baselineProfile {
  useConnectedDevices = false
  enableEmulatorDisplay = false
  managedDevices += "pixel9api36"
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
