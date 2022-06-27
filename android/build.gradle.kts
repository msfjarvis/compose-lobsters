@file:OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

plugins {
  id("dev.msfjarvis.claw.android-application")
  id("dev.msfjarvis.claw.rename-artifacts")
  id("dev.msfjarvis.claw.kotlin-android")
  id("dev.msfjarvis.claw.kotlin-kapt")
  alias(libs.plugins.compose)
  alias(libs.plugins.hilt)
}

android {
  namespace = "dev.msfjarvis.claw.android"
  defaultConfig {
    applicationId = "dev.msfjarvis.claw.android"
    versionCode = 1
    versionName = "1.0"
  }
  buildTypes {
    create("benchmark") {
      signingConfig = signingConfigs.getByName("debug")
      matchingFallbacks += listOf("release")
      isDebuggable = false
    }
  }
}

dependencies {
  kapt(libs.dagger.hilt.compiler)
  kapt(libs.androidx.hilt.compiler)
  implementation(projects.api)
  implementation(projects.common)
  implementation(projects.database)
  implementation(compose.material3)
  implementation(libs.accompanist.swiperefresh)
  implementation(libs.accompanist.sysuicontroller)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.core.splashscreen)
  implementation(libs.androidx.hilt.work)
  implementation(libs.androidx.lifecycle.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.paging.compose)
  implementation(libs.coil)
  implementation(libs.copydown)
  implementation(libs.dagger.hilt.android)
  implementation(libs.material.motion.core)
  implementation(libs.material.motion.navigation)
  implementation(libs.sqldelight.extensions.coroutines)
  implementation(libs.kotlin.coroutines.core)
  implementation(libs.kotlinx.datetime)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.retrofit.kotlinxSerializationConverter)
  implementation(libs.androidx.work.runtime.ktx)
}
