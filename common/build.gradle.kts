plugins {
  kotlin("multiplatform")
  id("com.android.library")
  id("org.jetbrains.compose") version "0.4.0-build173"
  `lobsters-plugin`
}

repositories {
  mavenCentral()
  maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  google()
}


tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_1_8.toString()
    useIR = true
  }
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
        implementation(Dependencies.AndroidX.Compose.runtime)
        implementation(Dependencies.AndroidX.browser)
      }
    }

    val commonMain by getting {
      dependencies {
        implementation(Dependencies.AndroidX.Compose.runtime)
      }
    }

    val jvmMain by getting {
      dependencies {
        implementation(Dependencies.AndroidX.Compose.runtime)
      }
    }
  }
}
