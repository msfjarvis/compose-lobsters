/*
 * Copyright © 2021-2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

plugins {
  id("dev.msfjarvis.claw.kotlin-android")
  id("dev.msfjarvis.claw.android-library")
  alias(libs.plugins.anvil)
  alias(libs.plugins.sqldelight)
  alias(libs.plugins.whetstone)
}

anvil { generateDaggerFactories.set(true) }

android { namespace = "dev.msfjarvis.claw.database" }

sqldelight {
  databases {
    create("LobstersDatabase") {
      packageName.set("dev.msfjarvis.claw.database")
      sourceFolders.set(listOf("sqldelight"))
      schemaOutputDirectory.set(file("src/main/sqldelight/databases"))
      verifyMigrations.set(true)
    }
  }
}

dependencies {
  implementation(libs.dagger)
  implementation(projects.core)
  implementation(libs.sqldelight.androidDriver)
  implementation(libs.sqldelight.primitiveAdapters)
  testImplementation(libs.kotest.assertions.core)
  testImplementation(libs.kotest.runner.junit5)
  testImplementation(libs.kotlinx.coroutines.core)
  testImplementation(libs.sqldelight.jvmDriver)
}
