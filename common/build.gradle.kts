import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("multiplatform")
  id("org.jetbrains.compose") version "1.0.0-alpha4-build331"
  id("com.android.library")
}

kotlin {
  android() { compilations.all { kotlinOptions.jvmTarget = "11" } }
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
      }
    }
    val commonTest by getting
    val androidMain by getting {
      dependencies {
        implementation(libs.androidx.browser)
        implementation(libs.coil.compose)
      }
    }
    val androidTest by getting { dependsOn(androidAndroidTestRelease) }
    val desktopMain by getting { dependencies { implementation(libs.kamel.image) } }
    val desktopTest by getting
  }
}

tasks.withType<KotlinCompile> {
  kotlinOptions.freeCompilerArgs =
    kotlinOptions.freeCompilerArgs + listOf("-Xopt-in=kotlin.RequiresOptIn")
}

android {
  buildFeatures { androidResources = true }
  compileSdk = 31
  sourceSets["main"].apply {
    manifest.srcFile("src/androidMain/AndroidManifest.xml")
    res.srcDirs("src/commonMain/resources")
  }
  defaultConfig {
    minSdk = 23
    targetSdk = 31
  }
}
