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
  implementation(platform(libs.okhttp.bom))
  implementation(libs.eithernet)
  implementation(libs.eithernet.integration.retrofit)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.kotlinx.datetime)
  implementation(libs.kotlinx.serialization.core)
  api(libs.kotlinx.serialization.json)
  api(libs.okhttp.core)
  api(libs.okhttp.loggingInterceptor)
  api(libs.retrofit)
  implementation(libs.retrofit.kotlinxSerializationConverter)
  api(projects.core.network.api)
  api(projects.core.parser.api)
  implementation(projects.model)

  testImplementation(libs.eithernet.test.fixtures)
  testImplementation(libs.kotlin.reflect)
  testImplementation(libs.kotlinx.coroutines.test)
  implementation(projects.ziplineParser)
  addTestDependencies(project)
}
