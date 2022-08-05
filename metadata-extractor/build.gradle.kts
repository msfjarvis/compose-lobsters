@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

plugins {
  kotlin("jvm")
  id("dev.msfjarvis.claw.kotlin-library")
}

dependencies {
  api(libs.crux)
  implementation(projects.model)
  implementation(libs.dagger.hilt.core)
  implementation(libs.jsoup)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.okhttp.core)
}
