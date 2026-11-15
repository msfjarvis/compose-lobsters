/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("UnstableApiUsage")

import dev.msfjarvis.claw.gradle.addTestDependencies

plugins {
  id("dev.msfjarvis.claw.android-library")
  id("dev.msfjarvis.claw.kotlin-android")
  id("dev.msfjarvis.claw.android-screenshot")
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.kotlin.composeCompiler)
  alias(libs.plugins.metro)
}

android { namespace = "dev.msfjarvis.claw.feature.discussion.impl" }

dependencies {
  api(projects.core.common)
  api(projects.core.navigation.api)
  api(projects.core.network.api)
  api(projects.core.ui)
  api(projects.model)
  api(libs.androidx.compose.foundation)
  api(libs.androidx.compose.foundation.layout)
  api(libs.androidx.compose.runtime)
  api(libs.androidx.compose.ui.text)
  api(libs.androidx.lifecycle.viewmodel)
  api(libs.androidx.navigation3.runtime)
  api(libs.kotlinx.coroutines.core)
  api(libs.metrox.viewmodel)

  implementation(projects.core.database.api)
  implementation(projects.feature.discussion.api)
  implementation(libs.androidx.compose.animation)
  implementation(libs.androidx.compose.animation.core)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.material3.adaptive)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.geometry)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.unit)
  implementation(libs.androidx.lifecycle.common)
  implementation(libs.androidx.lifecycle.compose)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.material3.navigation3)
  implementation(libs.eithernet)
  implementation(libs.kotlinResult)
  implementation(libs.kotlinResult.coroutines)
  implementation(libs.kotlinx.collections.immutable)
  implementation(libs.ksoup)
  implementation(libs.metrox.viewmodel.compose)

  compileOnly(libs.androidx.compose.ui.tooling.preview)

  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.okhttp.core)

  androidTestImplementation(libs.androidx.compose.ui.test)

  addTestDependencies(project)
}
