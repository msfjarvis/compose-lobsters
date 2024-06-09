/*
 * Copyright © 2021-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
plugins {
  id("dev.msfjarvis.claw.kotlin-jvm")
  alias(libs.plugins.anvil)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.poko)
  alias(libs.plugins.ksp)
}

anvil { generateDaggerFactories.set(true) }

dependencies {
  compileOnly(libs.konvert.annotations)
  api(libs.dagger)
  api(libs.javax.inject)
  api(projects.database.core)
  implementation(libs.konvert.api)
  implementation(libs.kotlinx.serialization.core)

  ksp(libs.konvert.processor)
}
