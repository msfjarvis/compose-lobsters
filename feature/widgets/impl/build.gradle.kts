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
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.kotlin.composeCompiler)
  alias(libs.plugins.metro)
}

android {
  namespace = "dev.msfjarvis.claw.feature.widgets.impl"
  androidResources.enable = true
  buildFeatures.buildConfig = true
}

dependencies {
  api(projects.core.common)
  api(projects.core.work.api)
  api(projects.feature.posts.api)
  api(projects.feature.tags.api)
  api(projects.feature.widgets.api)
  api(projects.model)
  api(libs.androidx.compose.glance)
  api(libs.androidx.compose.glance.core)
  api(libs.androidx.compose.runtime)
  api(libs.androidx.work.runtime)
  api(libs.kotlinx.collections.immutable)
  api(libs.kotlinx.coroutines.core)
  api(libs.metrox.android)

  implementation(projects.core.ui)
  implementation(libs.androidx.annotation)
  implementation(libs.androidx.collection)
  implementation(libs.androidx.compose.glance.m3)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui.text)
  implementation(libs.androidx.compose.ui.unit)
  implementation(libs.androidx.core)
  implementation(libs.sentry)

  compileOnly(libs.androidx.compose.glance.preview)

  runtimeOnly(libs.sentry.android.core)

  testImplementation(libs.kotlinx.coroutines.test)

  addTestDependencies(project)
}
