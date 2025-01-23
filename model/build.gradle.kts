/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
plugins {
  id("dev.msfjarvis.claw.kotlin-jvm")
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.poko)
  alias(libs.plugins.ksp)
  alias(libs.plugins.dependencyAnalysis)
}

dependencies {
  compileOnly(libs.konvert.annotations)
  api(projects.database.core)
  implementation(libs.konvert.api)
  implementation(libs.kotlinx.serialization.core)

  ksp(libs.konvert.processor)
}
