/*
 * Copyright © 2021-2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

plugins {
  kotlin("android")
  alias(libs.plugins.sqldelight)
  id("dev.msfjarvis.claw.kotlin-common")
  id("dev.msfjarvis.claw.android-library")
  alias(libs.plugins.anvil)
  alias(libs.plugins.whetstone)
}

anvil { generateDaggerFactories.set(true) }

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
  implementation(libs.dagger)
  implementation(projects.core)
  implementation(projects.diScopes)
  implementation(libs.sqldelight.androidDriver)
  implementation(libs.sqldelight.primitiveAdapters)
  testImplementation(libs.sqldelight.jvmDriver)
  testImplementation(libs.kotlinx.coroutines.core)
  testImplementation(kotlin("test-junit"))
}
