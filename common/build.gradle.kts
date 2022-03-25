@file:OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

import org.jetbrains.compose.compose

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.library)
  alias(libs.plugins.compose)
}

kotlin {
  android()
  jvm("desktop")
  sourceSets["commonMain"].apply {
    dependencies {
      api(compose.runtime)
      api(compose.foundation)
      api(compose.material)
      api(compose.material3)
      api(projects.database)
      api(projects.model)
      implementation(libs.kotlin.coroutines.core)
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
