/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("UnstableApiUsage")

plugins {
  id("dev.msfjarvis.claw.android-library")
  id("dev.msfjarvis.claw.kotlin-android")
  alias(libs.plugins.anvil)
}

android { namespace = "dev.msfjarvis.claw.store" }

anvil { generateDaggerFactories.set(true) }

dependencies {
  api(projects.database)
  api(projects.model)
  api(projects.core)

  implementation(projects.api)
  implementation(libs.dagger)
  implementation(libs.javax.inject)
  implementation(libs.kotlinx.atomicfu)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.sqldelight.extensions.coroutines)
  implementation(libs.store5)
}
