import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("multiplatform")
  id("org.jetbrains.compose") version "1.0.0-beta6-dev474"
  id("com.android.library")
}

kotlin {
  android { compilations.all { kotlinOptions.jvmTarget = "11" } }
  jvm("desktop") { compilations.all { kotlinOptions.jvmTarget = "11" } }
  sourceSets["commonMain"].apply {
    dependencies {
      api(compose.runtime)
      api(compose.foundation)
      api(compose.material)
      api(projects.database)
      api(projects.model)
      implementation(libs.compose.richtext.markdown)
      implementation(libs.compose.richtext.material)
      implementation(libs.compose.richtext.ui)
    }
  }
  sourceSets["androidMain"].apply {
    dependencies {
      implementation(libs.androidx.browser)
      implementation(libs.coil.compose)
    }
  }
  sourceSets["desktopMain"].apply { dependencies { implementation(libs.kamel.image) } }
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
