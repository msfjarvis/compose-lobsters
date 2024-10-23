/*
 * Copyright © 2021-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("UnstableApiUsage")

import com.android.build.api.variant.HasUnitTestBuilder
import dev.msfjarvis.claw.gradle.addTestDependencies
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag

plugins {
  id("dev.msfjarvis.claw.android-library")
  id("dev.msfjarvis.claw.kotlin-android")
  alias(libs.plugins.anvil)
  alias(libs.plugins.whetstone)
  alias(libs.plugins.kotlin.composeCompiler)
  alias(libs.plugins.screenshot)
}

android {
  buildFeatures {
    androidResources = true
    compose = true
  }
  namespace = "dev.msfjarvis.claw.common"
  experimentalProperties["android.experimental.enableScreenshotTest"] = true
}

androidComponents { beforeVariants { (it as HasUnitTestBuilder).enableUnitTest = false } }

anvil { generateDaggerFactories.set(true) }

composeCompiler { featureFlags.addAll(ComposeFeatureFlag.OptimizeNonSkippingGroups) }

dependencies {
  api(libs.androidx.compose.ui)
  api(libs.dagger)
  api(libs.javax.inject)
  api(projects.core)
  api(projects.database.core)
  api(projects.model)

  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.browser)
  implementation(libs.androidx.compose.animation)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.material3.adaptive)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.ui.text)
  implementation(libs.androidx.core)
  implementation(libs.coil)
  implementation(libs.coil.compose)
  implementation(libs.compose.richtext.markdown)
  implementation(libs.compose.richtext.material3)
  implementation(libs.compose.richtext.ui)
  implementation(libs.kotlinx.collections.immutable)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.kotlinResult)
  implementation(libs.kotlinResult.coroutines)
  implementation(libs.napier)

  screenshotTestImplementation(libs.androidx.compose.ui.tooling)

  compileOnly(libs.androidx.compose.ui.tooling.preview)

  runtimeOnly(libs.androidx.compose.ui.tooling)

  addTestDependencies(project)
}
