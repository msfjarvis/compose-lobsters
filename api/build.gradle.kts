@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

plugins {
  kotlin("jvm")
  id("dev.msfjarvis.claw.kotlin-library")
}

dependencies {
  api(projects.model)
  api(libs.retrofit.lib)
  implementation(libs.kotlinx.serialization.core)
  testImplementation(libs.kotlinx.serialization.json)
  testImplementation(libs.kotlin.coroutines.core)
  testImplementation(kotlin("test-junit"))
  testImplementation(libs.retrofit.kotlinxSerializationConverter)
  testImplementation(libs.retrofit.mock)
}
