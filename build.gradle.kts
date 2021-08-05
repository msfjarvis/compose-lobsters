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
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")
    classpath("com.android.tools.build:gradle:7.1.0-alpha06")
    classpath("com.diffplug.spotless:spotless-plugin-gradle:5.14.2")
    classpath("com.google.dagger:hilt-android-gradle-plugin:2.38.1")
    classpath("com.android.tools:r8:3.1.15-dev")
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
      ktfmt("0.27").googleStyle()
    }
    kotlinGradle {
      target("*.gradle.kts")
      ktfmt("0.27").googleStyle()
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
  gradleVersion = "7.1.1"
  distributionSha256Sum = "9bb8bc05f562f2d42bdf1ba8db62f6b6fa1c3bf6c392228802cc7cb0578fe7e0"
}
