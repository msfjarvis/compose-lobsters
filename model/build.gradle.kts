/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
plugins {
  id("dev.msfjarvis.claw.kotlin-jvm")
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.poko)
  alias(libs.plugins.ksp)
}

dependencies {
  implementation(libs.kotlinx.serialization.core)
  implementation(libs.konvert.api)
  implementation(projects.database.core)

  ksp(libs.konvert.processor)
}
