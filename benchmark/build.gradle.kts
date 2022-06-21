@file:Suppress("DSL_SCOPE_VIOLATION")

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
    // This benchmark buildType is used for benchmarking, and should function like your
    // release build (for example, with minification on). It's signed with a debug key
    // for easy local/CI testing.
    create("benchmark") {
      isDebuggable = true
      signingConfig = getByName("debug").signingConfig
      matchingFallbacks += listOf("release")
      setProguardFiles(
        listOf(
          "proguard-android-optimize.pro",
          "proguard-rules.pro",
          "proguard-rules-missing-classes.pro",
        )
      )
    }
  }

  targetProjectPath = ":android"
  experimentalProperties["android.experimental.self-instrumenting"] = true

  testOptions {
    managedDevices {
      devices {
        register<ManagedVirtualDevice>("pixel2api30") {
          device = "Pixel 2"
          apiLevel = 30
          systemImageSource = "aosp"
          require64Bit = true
        }
      }
    }
  }
}

dependencies {
  implementation(libs.androidx.benchmark.macro.junit4)
  implementation(libs.androidx.profileinstaller)
  implementation(libs.androidx.test.core)
  implementation(libs.androidx.test.ext.junit)
  implementation(libs.androidx.test.espresso.core)
  implementation(libs.androidx.test.rules)
  implementation(libs.androidx.test.runner)
  implementation(libs.androidx.test.uiautomator)
}

androidComponents { beforeVariants(selector().all()) { it.enable = it.buildType == "benchmark" } }
