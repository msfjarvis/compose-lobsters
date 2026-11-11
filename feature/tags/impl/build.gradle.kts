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
  api(projects.feature.tags.api)
  implementation(projects.core.common)
  implementation(projects.core.database.api)
  implementation(projects.core.network.api)
  implementation(projects.core.ui)
  implementation(projects.core.work.api)
  implementation(projects.model)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.foundation.layout)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.lifecycle.compose)
  implementation(libs.androidx.lifecycle.viewmodel)
  implementation(libs.androidx.work.runtime)
  implementation(libs.kotlinx.collections.immutable)
  implementation(libs.kotlinResult)
  implementation(libs.kotlinResult.coroutines)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.metrox.android)
  implementation(libs.metrox.viewmodel)
  implementation(libs.metrox.viewmodel.compose)

  testImplementation(projects.core.database.impl)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.sqldelight.jvmDriver)

  addTestDependencies(project)
}
