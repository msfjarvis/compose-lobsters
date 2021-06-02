plugins {
  id("org.jetbrains.compose") version "0.4.0"
  id("com.android.application")
  kotlin("android")
}

group = "dev.msfjarvis.claw"

version = "1.0"

repositories { google() }

dependencies {
  implementation(projects.common)
  implementation("androidx.activity:activity-compose:1.3.0-beta01")
}

android {
  compileSdkVersion(30)
  defaultConfig {
    applicationId = "dev.msfjarvis.claw.android"
    minSdkVersion(23)
    targetSdkVersion(30)
    versionCode = 1
    versionName = "1.0"
  }
  buildTypes { getByName("release") { isMinifyEnabled = false } }
}
