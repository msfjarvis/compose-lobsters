import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
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
    classpath("com.android.tools:r8:3.1.17-dev")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")
    classpath("com.android.tools.build:gradle:7.1.0-alpha10")
    classpath("com.diffplug.spotless:spotless-plugin-gradle:5.14.2")
    classpath("com.google.dagger:hilt-android-gradle-plugin:2.38.1")
  }
}

group = "dev.msfjarvis.claw"

version = "1.0"

allprojects {
  apply(plugin = "com.diffplug.spotless")
  configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
      target("**/*.kt")
      targetExclude("**/build/**")
      ktfmt("0.28").googleStyle()
    }
    kotlinGradle {
      target("*.gradle.kts")
      ktfmt("0.28").googleStyle()
    }
    format("xml") {
      target("**/*.xml")
      targetExclude("**/build/**", ".idea/**")
      prettier(mapOf("prettier" to "2.0.5", "@prettier/plugin-xml" to "0.13.0"))
        .config(mapOf("parser" to "xml", "tabWidth" to 2))
    }
  }
  tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
      jvmTarget = JavaVersion.VERSION_11.toString()
      languageVersion = "1.5"
      freeCompilerArgs = freeCompilerArgs + listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
  }
}

tasks.withType<Wrapper> {
  gradleVersion = "7.2"
  distributionSha256Sum = "f581709a9c35e9cb92e16f585d2c4bc99b2b1a5f85d2badbd3dc6bff59e1e6dd"
}
