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
  implementation(platform(libs.okhttp.bom))
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.okhttp.core)
  implementation(libs.okio)
  implementation(libs.zipline)
  implementation(libs.zipline.loader)
  implementation(projects.core.common)
  api(projects.core.parser.api)
  implementation(projects.ziplineParser)

  testImplementation(libs.kotlinx.coroutines.test)

  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.rules)
  androidTestImplementation(libs.androidx.test.runner)

  addTestDependencies(project)
}
