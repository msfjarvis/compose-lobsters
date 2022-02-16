import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  val kotlinVersion = "1.6.10"
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
    maven {
      url = uri("https://storage.googleapis.com/r8-releases/raw")
      content { includeModule("com.android.tools", "r8") }
    }
  }
  dependencies {
    classpath("com.android.tools:r8:3.3.14-dev")
    classpath(kotlin("gradle-plugin", version = kotlinVersion))
    classpath(kotlin("serialization", version = kotlinVersion))
    classpath("com.android.tools.build:gradle:7.0.4")
    classpath("com.google.dagger:hilt-android-gradle-plugin:2.40.5")
  }
}

plugins { id("com.diffplug.spotless") version "6.3.0" }

group = "dev.msfjarvis.claw"

version = "1.0"

spotless {
  kotlin {
    target("**/*.kt")
    targetExclude("**/build/**")
    ktfmt("0.32").googleStyle()
  }
  kotlinGradle {
    target("**/*.gradle.kts")
    ktfmt("0.32").googleStyle()
  }
}

allprojects {
  tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
  }
  tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
      jvmTarget = JavaVersion.VERSION_11.toString()
      languageVersion = "1.5"
      freeCompilerArgs =
        freeCompilerArgs +
          listOf(
            "-Xopt-in=kotlin.RequiresOptIn",
          )
    }
  }
}
