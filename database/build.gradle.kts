@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

plugins {
  kotlin("android")
  alias(libs.plugins.sqldelight)
  id("dev.msfjarvis.claw.kotlin-common")
  id("dev.msfjarvis.claw.android-library")
}

android { namespace = "dev.msfjarvis.claw.database" }

sqldelight {
  database("LobstersDatabase") {
    packageName = "dev.msfjarvis.claw.database"
    sourceFolders = listOf("sqldelight")
    schemaOutputDirectory = file("src/main/sqldelight/databases")
    verifyMigrations = true
  }
}

dependencies {
  implementation(libs.sqldelight.androidDriver)
  implementation(libs.sqldelight.primitiveAdapters)
  testImplementation(libs.sqldelight.jvmDriver)
  testImplementation(libs.kotlinx.coroutines.core)
  testImplementation(kotlin("test-junit"))
}
