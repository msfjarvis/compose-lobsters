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
  implementation(projects.feature.widgets.api)
  implementation(projects.feature.posts.api)
  implementation(projects.feature.tags.api)
  implementation(projects.core.common)
  implementation(projects.core.ui)
  implementation(projects.core.work.api)
  implementation(projects.model)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.glance)
  implementation(libs.androidx.compose.glance.core)
  implementation(libs.androidx.compose.glance.m3)
  implementation(libs.androidx.core)
  implementation(libs.androidx.work.runtime)
  implementation(libs.kotlinx.collections.immutable)
  implementation(libs.metrox.android)
  implementation(libs.sentry.android.core)
  compileOnly(libs.androidx.compose.glance.preview)
  testImplementation(libs.kotlinx.coroutines.test)
  addTestDependencies(project)
}
