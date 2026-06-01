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
  api(libs.kotlinx.datetime)
  api(libs.kotlinx.serialization.core)
  api(libs.kspoon)
  api(projects.database.core)

  implementation(libs.ksoup)

  compileOnly(libs.konvert.annotations)

  ksp(libs.konvert.processor)
}
