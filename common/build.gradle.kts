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
  alias(libs.plugins.kotlin.composeCompiler)
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.metro)
}

android {
  androidResources.enable = true
  buildFeatures { compose = true }
  namespace = "dev.msfjarvis.claw.common"
}

dependencies {
  api(libs.androidx.compose.foundation.layout)
  api(libs.androidx.compose.ui)
  api(libs.androidx.compose.ui.graphics)
  api(libs.androidx.datastore)
  api(libs.androidx.datastore.core)
  api(libs.androidx.datastore.preferences.core)
  api(libs.androidx.lifecycle.viewmodel)
  api(projects.api)
  api(projects.core)
  api(projects.database.core)
  api(projects.model)

  implementation(platform(libs.okhttp.bom))
  implementation(libs.androidx.annotation)
  implementation(libs.androidx.browser)
  implementation(libs.androidx.compose.animation)
  implementation(libs.androidx.compose.animation.core)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.runtime.annotation)
  implementation(libs.androidx.compose.ui.text)
  implementation(libs.androidx.compose.ui.unit)
  implementation(libs.androidx.core)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.common)
  implementation(libs.androidx.lifecycle.compose)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.coil3.compose)
  implementation(libs.coil3.core)
  implementation(libs.coil3.network.okhttp)
  implementation(libs.coil3.plain)
  implementation(libs.eithernet)
  implementation(libs.htmlconverter)
  implementation(libs.jsoup)
  implementation(libs.kotlinResult)
  implementation(libs.kotlinResult.coroutines)
  implementation(libs.kotlinx.collections.immutable)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.metrox.viewmodel)
  implementation(libs.metrox.viewmodel.compose)
  implementation(libs.sqldelight.runtime)

  compileOnly(libs.androidx.compose.ui.tooling.preview)

  runtimeOnly(libs.androidx.compose.ui.tooling)

  addTestDependencies(project)
}
