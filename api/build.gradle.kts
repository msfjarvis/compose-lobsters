/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
import dev.msfjarvis.claw.gradle.addTestDependencies

plugins {
  id("dev.msfjarvis.claw.kotlin-jvm")
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.metro)
}

dependencies {
  api(libs.eithernet)
  api(libs.okhttp.core)
  api(libs.retrofit)
  api(projects.model)

  implementation(libs.eithernet.integration.retrofit)
  implementation(libs.kotlinx.serialization.core)
  implementation(libs.ksoup)
  implementation(libs.kspoon)
  implementation(libs.retrofit.kotlinxSerializationConverter)

  testImplementation(libs.eithernet.test.fixtures)
  testImplementation(libs.kotlin.reflect)
  testImplementation(libs.kotlinx.coroutines.test)
  addTestDependencies(project)
}
