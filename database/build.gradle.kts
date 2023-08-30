/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
import dev.msfjarvis.claw.gradle.addTestDependencies

plugins {
  id("dev.msfjarvis.claw.android-library")
  id("dev.msfjarvis.claw.kotlin-android")
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.anvil)
  alias(libs.plugins.sqldelight)
}

android { namespace = "dev.msfjarvis.claw.database" }

anvil { generateDaggerFactories.set(true) }

sqldelight {
  databases {
    create("LobstersDatabase") {
      packageName.set("dev.msfjarvis.claw.database")
      schemaOutputDirectory.set(file("src/main/sqldelight/databases"))
      verifyMigrations.set(true)
    }
  }
}

dependencies {
  anvil(libs.whetstone.compiler)

  implementation(libs.dagger)
  implementation(libs.sqldelight.androidDriver)
  implementation(libs.sqldelight.primitiveAdapters)
  implementation(libs.sqlite.android)
  implementation(libs.kotlinx.serialization.core)
  implementation(libs.whetstone)

  testImplementation(libs.sqldelight.jvmDriver)
  testImplementation(libs.kotlinx.serialization.json)
  addTestDependencies(project)
}
