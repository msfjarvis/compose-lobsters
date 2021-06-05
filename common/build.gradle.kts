import org.jetbrains.compose.compose

plugins {
  kotlin("multiplatform")
  id("org.jetbrains.compose") version "0.4.0"
  id("com.android.library")
}

kotlin {
  android()
  jvm("desktop") { compilations.all { kotlinOptions.jvmTarget = "11" } }
  sourceSets {
    // Workaround for:
    //
    // The Kotlin source set androidAndroidTestRelease was configured but not added to any
    // Kotlin compilation. You can add a source set to a target's compilation by connecting it
    // with the compilation's default source set using 'dependsOn'.
    // See
    // https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#connecting-source-sets
    //
    // This workaround includes `dependsOn(androidAndroidTestRelease)` in the `androidTest`
    // sourceSet.
    val androidAndroidTestRelease by getting
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
    val androidTest by getting { dependsOn(androidAndroidTestRelease) }
    val desktopMain by getting
    val desktopTest by getting
  }
}

android {
  buildFeatures { androidResources = true }
  compileSdkVersion(30)
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  defaultConfig {
    minSdkVersion(23)
    targetSdkVersion(30)
  }
}
