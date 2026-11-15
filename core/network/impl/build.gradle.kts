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
  api(projects.core.network.api)
  api(projects.core.parser.api)
  api(libs.kotlinx.serialization.json)
  api(libs.okhttp.core)
  api(libs.okhttp.loggingInterceptor)
  api(libs.retrofit)

  implementation(platform(libs.okhttp.bom))
  implementation(projects.model)
  implementation(projects.ziplineParser)
  implementation(libs.eithernet)
  implementation(libs.eithernet.integration.retrofit)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.kotlinx.datetime)
  implementation(libs.kotlinx.serialization.core)
  implementation(libs.retrofit.kotlinxSerializationConverter)

  testImplementation(libs.eithernet.test.fixtures)
  testImplementation(libs.kotlin.reflect)
  testImplementation(libs.kotlinx.coroutines.test)

  // Tests load HTML fixtures as resources, which dependency analysis cannot detect.
  testRuntimeOnly(testFixtures(projects.htmlTestFixtures))

  addTestDependencies(project)
}
