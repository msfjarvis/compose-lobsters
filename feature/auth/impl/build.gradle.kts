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
  implementation(platform(libs.okhttp.bom))
  api(projects.feature.auth.api)
  implementation(projects.core.common)
  implementation(projects.core.network.api)
  implementation(projects.core.database.api)
  implementation(projects.core.navigation.api)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.foundation.layout)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.ui)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.metrox.android)
  implementation(libs.okhttp.core)

  testImplementation(projects.core.database.impl)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.sqldelight.jvmDriver)

  addTestDependencies(project)
}
