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
  api(libs.kotlinx.serialization.core)
  api(libs.okhttp.core)
  api(libs.retrofit)
  api(projects.model)
  api(projects.ziplineParser)

  implementation(libs.eithernet.integration.retrofit)
  implementation(libs.zipline.loader)

  testImplementation(libs.eithernet.test.fixtures)
  testImplementation(libs.kotlin.reflect)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.kotlinx.datetime)
  addTestDependencies(project)
}
