plugins {
  kotlin("multiplatform")
  kotlin("plugin.serialization")
  id("com.android.library")
}

kotlin {
  android()
  jvm { compilations.all { kotlinOptions.jvmTarget = "11" } }
  sourceSets["commonMain"].apply {
    dependencies { implementation(libs.kotlinx.serialization.core) }
  }
}

android {
  compileSdk = 31
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  defaultConfig {
    minSdk = 23
    targetSdk = 31
    consumerProguardFiles("consumer-rules.pro")
  }
}
