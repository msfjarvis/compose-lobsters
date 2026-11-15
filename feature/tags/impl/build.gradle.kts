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

android {
  namespace = "dev.msfjarvis.claw.feature.tags.impl"
  androidResources.enable = true
  buildFeatures.buildConfig = true
}

dependencies {
  api(projects.core.common)
  api(projects.core.database.api)
  api(projects.core.network.api)
  api(projects.core.work.api)
  api(projects.feature.tags.api)
  api(projects.model)
  api(libs.androidx.compose.foundation.layout)
  api(libs.androidx.compose.runtime)
  api(libs.androidx.compose.ui)
  api(libs.androidx.lifecycle.viewmodel)
  api(libs.androidx.work.runtime)
  api(libs.kotlinx.collections.immutable)
  api(libs.kotlinx.coroutines.core)
  api(libs.metrox.viewmodel)

  implementation(projects.core.ui)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.text)
  implementation(libs.androidx.compose.ui.unit)
  implementation(libs.androidx.lifecycle.common)
  implementation(libs.androidx.lifecycle.compose)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.eithernet)
  implementation(libs.kotlinResult)
  implementation(libs.kotlinResult.coroutines)
  implementation(libs.kotlinx.datetime)
  implementation(libs.metrox.viewmodel.compose)

  testImplementation(projects.core.database.impl)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.okhttp.core)
  testImplementation(libs.sqldelight.jvmDriver)
  testImplementation(libs.sqldelight.runtime)

  addTestDependencies(project)
}
