/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("UnstableApiUsage")

import com.android.build.api.variant.HasUnitTestBuilder
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

androidComponents { beforeVariants { (it as HasUnitTestBuilder).enableUnitTest = false } }

dependencies {
  api(libs.androidx.compose.ui)
  api(libs.androidx.datastore)
  api(projects.api)
  api(projects.core)
  api(projects.database.core)
  api(projects.model)

  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.browser)
  implementation(libs.androidx.compose.animation)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.ui.text)
  implementation(libs.androidx.core)
  implementation(libs.coil3.compose)
  implementation(libs.coil3.network.okhttp)
  implementation(libs.htmlconverter)
  implementation(libs.kotlinx.collections.immutable)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.kotlinResult)
  implementation(libs.kotlinResult.coroutines)
  implementation(libs.napier)
  implementation(libs.metrox.viewmodel)
  implementation(libs.metrox.viewmodel.compose)

  compileOnly(libs.androidx.compose.ui.tooling.preview)

  runtimeOnly(libs.androidx.compose.ui.tooling)

  addTestDependencies(project)
}
