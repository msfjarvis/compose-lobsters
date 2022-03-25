@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

buildscript {
  repositories {
    maven {
      url = uri("https://storage.googleapis.com/r8-releases/raw")
      content { includeModule("com.android.tools", "r8") }
    }
  }
  dependencies { classpath(libs.r8) }
}

plugins {
  alias(libs.plugins.spotless)
  alias(libs.plugins.versions)
  alias(libs.plugins.vcu)
}

group = "dev.msfjarvis.claw"

version = "1.0"

spotless {
  kotlin {
    target("**/*.kt")
    targetExclude("**/build/**")
    ktfmt("0.35").googleStyle()
  }
  kotlinGradle {
    target("**/*.gradle.kts")
    ktfmt("0.35").googleStyle()
  }
}
