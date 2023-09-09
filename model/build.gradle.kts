/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
plugins {
  id("dev.msfjarvis.claw.android-library")
  id("dev.msfjarvis.claw.kotlin-android")
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.poko)
  alias(libs.plugins.ksp)
}

android { namespace = "dev.msfjarvis.claw.model" }

dependencies {
  implementation(libs.kotlinx.serialization.core)
  implementation(libs.konvert.api)
  implementation(projects.database)

  ksp(libs.konvert.processor)
}
