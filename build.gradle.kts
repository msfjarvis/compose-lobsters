@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

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

fun isNonStable(version: String): Boolean {
  val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
  val regex = "^[0-9,.v-]+(-r)?$".toRegex()
  val isStable = stableKeyword || regex.matches(version)
  return isStable.not()
}

tasks.withType<DependencyUpdatesTask>().configureEach {
  rejectVersionIf {
    when (candidate.group) {
      "com.android.application", "com.android.library" -> true
      else -> isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
  }
  checkForGradleUpdate = false
  checkBuildEnvironmentConstraints = true
  outputFormatter = "json"
  outputDir = "build/dependencyUpdates"
  reportfileName = "report"
}

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
