/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

plugins {
  kotlin("android")
  id("dev.msfjarvis.claw.kotlin-common")
  id("dev.msfjarvis.claw.android-library")
  alias(libs.plugins.anvil)
}

anvil { generateDaggerFactories.set(true) }

android { namespace = "dev.msfjarvis.claw.core" }

dependencies {
  api(libs.okhttp.loggingInterceptor)
  implementation(projects.diScopes)
  implementation(libs.dagger)
  implementation(libs.javax.inject)
  implementation(libs.napier)
  implementation(libs.okhttp.core)
}
