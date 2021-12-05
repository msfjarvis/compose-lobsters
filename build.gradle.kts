import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  val kotlinVersion = "1.5.31"
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
    classpath("com.android.tools:r8:3.2.28-dev")
    classpath(kotlin("gradle-plugin", version = kotlinVersion))
    classpath(kotlin("serialization", version = kotlinVersion))
    classpath("com.android.tools.build:gradle:7.0.3")
    classpath("com.diffplug.spotless:spotless-plugin-gradle:6.0.0")
    classpath("com.google.dagger:hilt-android-gradle-plugin:2.40.2")
  }
}

plugins { id("com.diffplug.spotless") version "6.0.0" }

group = "dev.msfjarvis.claw"

version = "1.0"

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
  kotlin {
    target("**/*.kt")
    targetExclude("**/build/**")
    ktfmt("0.30").googleStyle()
  }
  kotlinGradle {
    target("**/*.gradle.kts")
    ktfmt("0.30").googleStyle()
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

tasks.withType<Wrapper> {
  gradleVersion = "7.3"
  distributionSha256Sum = "de8f52ad49bdc759164f72439a3bf56ddb1589c4cde802d3cec7d6ad0e0ee410"
}
