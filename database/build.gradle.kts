plugins {
  kotlin("multiplatform")
  id("com.android.library")
  id("com.squareup.sqldelight") version "1.5.1"
}

kotlin {
  android()
  jvm("desktop") {
    compilations.all {
      kotlinOptions.jvmTarget = "11"
      kotlinOptions.freeCompilerArgs =
        kotlinOptions.freeCompilerArgs + listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
  }
  sourceSets["androidMain"].apply { dependencies { implementation(libs.sqldelight.androidDriver) } }
  sourceSets["desktopMain"].apply { dependencies { implementation(libs.sqldelight.jvmDriver) } }
  sourceSets["desktopTest"].apply {
    dependencies {
      implementation(libs.kotlin.coroutines.core)
      implementation(kotlin("test-junit"))
    }
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

configure<com.squareup.sqldelight.gradle.SqlDelightExtension> {
  database("LobstersDatabase") {
    packageName = "dev.msfjarvis.claw.database"
    sourceFolders = listOf("sqldelight")
  }
}
