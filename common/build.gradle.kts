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
  alias(libs.plugins.anvil)
  alias(libs.plugins.whetstone)
  alias(libs.plugins.kotlin.composeCompiler)
  alias(libs.plugins.dependencyAnalysis)
}

android {
  androidResources.enable = true
  buildFeatures { compose = true }
  namespace = "dev.msfjarvis.claw.common"
}

whetstone.addOns.compose = true

androidComponents { beforeVariants { (it as HasUnitTestBuilder).enableUnitTest = false } }

anvil { generateDaggerFactories.set(true) }

dependencies {
  api(libs.androidx.compose.ui)
  api(libs.androidx.datastore)
  api(libs.dagger)
  api(libs.javax.inject)
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
  implementation(libs.androidx.compose.runtime.saveable)
  implementation(libs.androidx.compose.ui.text)
  implementation(libs.androidx.core)
  implementation(libs.coil3.compose)
  implementation(libs.coil3.network.okhttp)
  implementation(libs.compose.richtext.markdown)
  implementation(libs.compose.richtext.material3)
  implementation(libs.compose.richtext.ui)
  implementation(libs.htmlconverter)
  implementation(libs.kotlinx.collections.immutable)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.kotlinResult)
  implementation(libs.kotlinResult.coroutines)
  implementation(libs.napier)

  compileOnly(libs.androidx.compose.ui.tooling.preview)

  runtimeOnly(libs.androidx.compose.ui.tooling)

  addTestDependencies(project)
}
