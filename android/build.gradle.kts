plugins {
  id("org.jetbrains.compose") version "0.4.0"
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
  id("dagger.hilt.android.plugin")
}

dependencies {
  kapt(libs.dagger.hilt.compiler)
  implementation(projects.api)
  implementation(projects.common)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.paging.compose)
  implementation(libs.dagger.hilt.android)
  implementation(libs.retrofit.moshiConverter)
  implementation(libs.moshix.metadatareflect)
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
