@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

plugins {
  kotlin("jvm")
  id("dev.msfjarvis.claw.kotlin-library")
}

dependencies {
  implementation(libs.crux)
  implementation(libs.dagger.hilt.core)
  implementation(libs.jsoup)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.okhttp.core)
}
