@file:Suppress("DSL_SCOPE_VIOLATION")

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
      signingConfig = getByName("debug").signingConfig
      matchingFallbacks += listOf("release")
    }
  }

  targetProjectPath = ":android"
  experimentalProperties["android.experimental.self-instrumenting"] = true
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

androidComponents { beforeVariants(selector().all()) { it.enable = it.buildType == "benchmark" } }
