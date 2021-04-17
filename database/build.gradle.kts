plugins {
  kotlin("multiplatform")
  id("com.android.library")
  id("com.squareup.sqldelight")
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
    val commonMain by getting {
    }
    val jvmTest by getting {
      dependencies {
        implementation(libs.kotlin.coroutines.core)
        implementation(kotlin("test-junit"))
      }
    }
    val jvmMain by getting {
      dependencies {
        implementation(libs.thirdparty.sqldelight.jvmDriver)
      }
    }
    val androidMain by getting {
      dependencies {
        implementation(libs.thirdparty.sqldelight.androidDriver)
      }
    }
  }
}

android {
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
}
