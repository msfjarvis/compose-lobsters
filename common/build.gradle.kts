import org.jetbrains.compose.compose

plugins {
  kotlin("multiplatform")
  id("org.jetbrains.compose") version "0.4.0"
  id("com.android.library")
}

group = "dev.msfjarvis.claw"

version = "1.0"

repositories { google() }

kotlin {
  android()
  jvm("desktop") { compilations.all { kotlinOptions.jvmTarget = "11" } }
  sourceSets {
    val commonMain by getting {
      dependencies {
        api(compose.runtime)
        api(compose.foundation)
        api(compose.material)
        api(projects.database)
        implementation("com.alialbaali.kamel:kamel-image:0.2.1")
      }
    }
    val commonTest by getting { dependencies { implementation(kotlin("test")) } }
    val androidMain by getting { dependencies { implementation(libs.androidx.browser) } }
    val androidTest by getting
    val desktopMain by getting
    val desktopTest by getting
  }
}

android {
  compileSdkVersion(30)
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  defaultConfig {
    minSdkVersion(23)
    targetSdkVersion(30)
  }
}
