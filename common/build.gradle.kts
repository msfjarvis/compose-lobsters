plugins {
  kotlin("multiplatform")
  id("com.android.library")
  id("org.jetbrains.compose")
  `lobsters-plugin`
}

// workaround for https://youtrack.jetbrains.com/issue/KT-43944
android {
  configurations {
    create("androidTestApi")
    create("androidTestDebugApi")
    create("androidTestReleaseApi")
    create("testApi")
    create("testDebugApi")
    create("testReleaseApi")
  }
}

kotlin {
  jvm()
  android()

  sourceSets {
    named("androidMain") {
      dependencies {
        implementation(libs.androidx.browser)
      }
    }

    named("commonMain") {
      dependencies {
        api(compose.runtime)
        api(compose.ui)
      }
    }

    named("androidTest") { dependencies { implementation(kotlin("test-junit")) } }

    named("jvmTest") { dependencies { implementation(kotlin("test-junit")) } }

    named("commonTest") {
      dependencies {
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
      }
    }
  }
}

android {
  buildFeatures { androidResources = true }

  sourceSets {
    named("main") {
      manifest.srcFile("src/androidMain/AndroidManifest.xml")
      res.srcDirs("src/androidMain/res")
    }
  }
}
