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
    classpath("com.android.tools:r8:3.2.16-dev")
    classpath(kotlin("gradle-plugin", version = kotlinVersion))
    classpath(kotlin("serialization", version = kotlinVersion))
    classpath("com.android.tools.build:gradle:7.2.0-alpha03")
    classpath("com.diffplug.spotless:spotless-plugin-gradle:5.17.0")
    classpath("com.google.dagger:hilt-android-gradle-plugin:2.39.1")
  }
}

group = "dev.msfjarvis.claw"

version = "1.0"

apply(plugin = "com.diffplug.spotless")

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
  kotlin {
    target("**/*.kt")
    targetExclude("**/build/**")
    ktfmt("0.29").googleStyle()
  }
  kotlinGradle {
    target("**/*.gradle.kts")
    ktfmt("0.29").googleStyle()
  }
}

allprojects {
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
  gradleVersion = "7.3-rc-3"
}
