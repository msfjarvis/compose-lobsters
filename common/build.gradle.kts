plugins {
  kotlin("multiplatform")
  id("com.android.library")
  id("org.jetbrains.compose") version "0.4.0-build177"
  `lobsters-plugin`
}

repositories {
  maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
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
    val androidMain by getting {
      dependencies {
        implementation(compose.runtime)
        implementation(Dependencies.AndroidX.browser)
      }
    }

    val commonMain by getting {
      dependencies {
        implementation(compose.runtime)
        implementation(compose.ui)
      }
    }

    val jvmMain by getting {
      dependencies {
        implementation(compose.runtime)
      }
    }

    val androidTest by getting {
      dependencies {
        implementation(kotlin("test-junit"))
      }
    }

    val jvmTest by getting {
      dependencies {
        implementation(kotlin("test-junit"))
      }
    }

    val commonTest by getting {
      dependencies {
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
      }
    }
  }
}

android {
  buildFeatures {
    androidResources = true
  }

  sourceSets {
    named("main") {
      manifest.srcFile("src/androidMain/AndroidManifest.xml")
      res.srcDirs("src/androidMain/res")
    }
  }
}
