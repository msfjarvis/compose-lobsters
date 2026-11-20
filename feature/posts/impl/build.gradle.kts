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
  id("dev.msfjarvis.claw.android-screenshot")
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.kotlin.composeCompiler)
  alias(libs.plugins.metro)
}

android {
  namespace = "dev.msfjarvis.claw.feature.posts.impl"
  androidResources.enable = true
}

dependencies {
  api(projects.core.common)
  api(projects.core.database.api)
  api(projects.core.navigation.api)
  api(projects.core.network.api)
  api(projects.core.ui)
  api(projects.core.work.api)
  api(projects.feature.posts.api)
  api(projects.model)
  api(libs.androidx.compose.foundation)
  api(libs.androidx.compose.foundation.layout)
  api(libs.androidx.compose.material3.adaptive.layout)
  api(libs.androidx.compose.runtime)
  api(libs.androidx.compose.ui)
  api(libs.androidx.lifecycle.viewmodel)
  api(libs.androidx.navigation3.runtime)
  api(libs.androidx.paging.common)
  api(libs.androidx.paging.compose)
  api(libs.androidx.work.runtime)
  api(libs.eithernet)
  api(libs.kotlinx.collections.immutable)
  api(libs.kotlinx.coroutines.core)
  api(libs.metrox.viewmodel)
  api(libs.unfurl)

  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.material3.adaptive)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.text)
  implementation(libs.androidx.compose.ui.unit)
  implementation(libs.androidx.material3.navigation3)
  implementation(libs.kotlinx.datetime)
  implementation(libs.okhttp.core)
  implementation(libs.swipe)

  screenshotTestImplementation(libs.coil3.compose)

  testImplementation(libs.kotlinx.coroutines.test)

  androidTestImplementation(libs.androidx.compose.ui.test)
  androidTestImplementation(libs.coil3.compose)

  addTestDependencies(project)
}
