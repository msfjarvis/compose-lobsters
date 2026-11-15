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
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.text)
  implementation(libs.androidx.compose.ui.unit)
  implementation(libs.androidx.lifecycle.common)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.eithernet)
  implementation(libs.kotlinx.datetime)
  testImplementation(libs.okhttp.core)
  testImplementation(libs.sqldelight.runtime)
  api(projects.feature.tags.api)
  api(projects.core.common)
  api(projects.core.database.api)
  api(projects.core.network.api)
  implementation(projects.core.ui)
  api(projects.core.work.api)
  api(projects.model)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.foundation)
  api(libs.androidx.compose.foundation.layout)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  api(libs.androidx.compose.runtime)
  api(libs.androidx.compose.ui)
  implementation(libs.androidx.lifecycle.compose)
  api(libs.androidx.lifecycle.viewmodel)
  api(libs.androidx.work.runtime)
  api(libs.kotlinx.collections.immutable)
  implementation(libs.kotlinResult)
  implementation(libs.kotlinResult.coroutines)
  api(libs.kotlinx.coroutines.core)
  api(libs.metrox.viewmodel)
  implementation(libs.metrox.viewmodel.compose)

  testImplementation(projects.core.database.impl)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.sqldelight.jvmDriver)

  addTestDependencies(project)
}
