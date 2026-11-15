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
  alias(libs.plugins.kotlin.composeCompiler)
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.metro)
}

android { namespace = "dev.msfjarvis.claw.feature.auth.impl" }

dependencies {
  api(libs.androidx.navigation3.runtime)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.text)
  implementation(libs.androidx.compose.ui.unit)
  testImplementation(libs.sqldelight.runtime)
  implementation(platform(libs.okhttp.bom))
  api(projects.feature.auth.api)
  api(projects.core.network.api)
  api(projects.core.database.api)
  api(projects.core.navigation.api)
  implementation(libs.androidx.compose.foundation)
  api(libs.androidx.compose.foundation.layout)
  implementation(libs.androidx.compose.material3)
  api(libs.androidx.compose.runtime)
  api(libs.androidx.compose.ui)
  api(libs.kotlinx.coroutines.core)
  implementation(libs.okhttp.core)

  testImplementation(projects.core.database.impl)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.sqldelight.jvmDriver)

  addTestDependencies(project)
}
