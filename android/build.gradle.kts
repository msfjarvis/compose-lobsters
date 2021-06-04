plugins {
  id("org.jetbrains.compose") version "0.4.0"
  id("com.android.application")
  kotlin("android")
}

dependencies {
  implementation(projects.api)
  implementation(projects.common)
  implementation("androidx.activity:activity-compose:1.3.0-beta01")
  implementation("androidx.appcompat:appcompat:1.4.0-alpha02")
  implementation("androidx.paging:paging-compose:1.0.0-alpha10")
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
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
}
