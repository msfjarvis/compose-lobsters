/*
 * Copyright © 2022-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("UnstableApiUsage")

import com.android.build.api.dsl.ManagedVirtualDevice

plugins {
  alias(libs.plugins.android.test)
  id("dev.msfjarvis.claw.android-common")
  id("dev.msfjarvis.claw.kotlin-android")
}

android {
  namespace = "dev.msfjarvis.claw.benchmark"

  defaultConfig { testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }

  buildTypes {
    create("benchmark") {
      isDebuggable = true
      signingConfig = signingConfigs["debug"]
      matchingFallbacks += "release"
    }
  }

  testOptions {
    managedDevices {
      devices {
        create<ManagedVirtualDevice>("api31") {
          device = "Pixel 6"
          apiLevel = 31
          systemImageSource = "aosp"
        }
      }
    }
  }

  targetProjectPath = ":android"
  experimentalProperties["android.experimental.self-instrumenting"] = true
}

androidComponents { beforeVariants(selector().all()) { it.enable = it.buildType == "benchmark" } }

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
