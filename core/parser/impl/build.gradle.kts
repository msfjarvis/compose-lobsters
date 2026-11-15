/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
import dev.msfjarvis.claw.gradle.addTestDependencies

plugins {
  id("dev.msfjarvis.claw.android-library")
  id("dev.msfjarvis.claw.kotlin-android")
  alias(libs.plugins.android.junit5)
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.metro)
  alias(libs.plugins.zipline)
}

android {
  namespace = "dev.msfjarvis.claw.core.parser.impl"
  defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
}

dependencies {
  androidTestImplementation(libs.androidx.monitor)
  implementation(libs.kotlinx.serialization.core)
  implementation(projects.model)
  implementation(platform(libs.okhttp.bom))
  implementation(libs.kotlinx.coroutines.core)
  api(libs.okhttp.core)
  implementation(libs.okio)
  implementation(libs.zipline)
  implementation(libs.zipline.loader)
  api(projects.core.parser.api)
  api(projects.ziplineParser)

  testImplementation(libs.kotlinx.coroutines.test)

  androidTestRuntimeOnly(libs.androidx.test.runner)

  addTestDependencies(project)
}
