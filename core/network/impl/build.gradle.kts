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
  testImplementation(testFixtures(projects.htmlTestFixtures))

  implementation(platform(libs.okhttp.bom))
  implementation(libs.eithernet)
  implementation(libs.eithernet.integration.retrofit)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.kotlinx.datetime)
  implementation(libs.kotlinx.serialization.core)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.okhttp.core)
  implementation(libs.okhttp.loggingInterceptor)
  implementation(libs.retrofit)
  implementation(libs.retrofit.kotlinxSerializationConverter)
  implementation(projects.core.network.api)
  implementation(projects.core.parser.api)
  implementation(projects.model)

  testImplementation(libs.eithernet.test.fixtures)
  testImplementation(libs.kotlin.reflect)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(projects.ziplineParser)
  addTestDependencies(project)
}
