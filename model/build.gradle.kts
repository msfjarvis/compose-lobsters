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
  api(projects.database.core)

  implementation(libs.kotlinx.serialization.core)

  compileOnly(libs.konvert.annotations)

  ksp(libs.konvert.processor)
}
