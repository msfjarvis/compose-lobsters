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
  api(projects.feature.discussion.api)
  implementation(projects.feature.posts.api)
  implementation(projects.feature.profile.api)
  implementation(projects.feature.auth.api)
  implementation(projects.core.common)
  implementation(projects.core.database.api)
  implementation(projects.core.navigation.api)
  implementation(projects.core.network.api)
  implementation(projects.core.ui)
  implementation(projects.model)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.animation)
  implementation(libs.androidx.material3.navigation3)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.lifecycle.compose)
  implementation(libs.androidx.lifecycle.viewmodel)
  implementation(libs.kotlinResult)
  implementation(libs.kotlinResult.coroutines)
  implementation(libs.ksoup)
  implementation(libs.kotlinx.collections.immutable)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.kotlinx.datetime)
  implementation(libs.metrox.viewmodel)
  implementation(libs.metrox.viewmodel.compose)

  compileOnly(libs.androidx.compose.ui.tooling.preview)

  testImplementation(libs.kotlinx.coroutines.test)
  addTestDependencies(project)
}
