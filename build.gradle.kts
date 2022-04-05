@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

buildscript {
  repositories {
    maven {
      url = uri("https://storage.googleapis.com/r8-releases/raw")
      content { includeModule("com.android.tools", "r8") }
    }
  }
  dependencies {
    classpath(libs.r8)
    classpath(libs.javapoet)
  }
}

plugins {
  id("dev.msfjarvis.claw.spotless")
  id("dev.msfjarvis.claw.versions")
  id("dev.msfjarvis.claw.kotlin-common")
}
