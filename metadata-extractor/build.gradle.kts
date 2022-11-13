/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

plugins {
  kotlin("jvm")
  id("dev.msfjarvis.claw.kotlin-library")
}

dependencies {
  api(libs.crux)
  implementation(projects.model)
  implementation(libs.javax.inject)
  implementation(libs.jsoup)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.okhttp.core)
}
