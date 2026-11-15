/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
import dev.msfjarvis.claw.gradle.addTestDependencies

plugins {
  id("dev.msfjarvis.claw.android-library")
  id("dev.msfjarvis.claw.kotlin-android")
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.metro)
  alias(libs.plugins.sqldelight)
}

android { namespace = "dev.msfjarvis.claw.database" }

sqldelight {
  databases {
    create("LobstersDatabase") {
      packageName.set("dev.msfjarvis.claw.database")
      schemaOutputDirectory.set(file("src/main/sqldelight/databases"))
      verifyMigrations.set(true)
      dialect(libs.sqldelight.dialect338)
    }
  }
}

dependencies {
  api(projects.core.common)
  api(projects.core.database.api)
  api(projects.model)
  api(libs.kotlinx.coroutines.core)
  api(libs.kotlinx.serialization.core)
  api(libs.kotlinx.serialization.json)
  api(libs.sqldelight.runtime)

  implementation(libs.androidx.sqlite)
  implementation(libs.sentry.android.sqlite)
  implementation(libs.sqldelight.androidDriver)
  implementation(libs.sqldelight.extensions.coroutines)
  implementation(libs.sqldelight.primitiveAdapters)
  implementation(libs.sqlite.android)

  testImplementation(libs.sqldelight.jvmDriver)
  testImplementation(libs.sqldelight.runtime)

  addTestDependencies(project)
}
