/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("UnstableApiUsage")

import dev.msfjarvis.claw.gradle.addTestDependencies

plugins {
  id("dev.msfjarvis.claw.android-application")
  id("dev.msfjarvis.claw.rename-artifacts")
  id("dev.msfjarvis.claw.kotlin-android")
  id("dev.msfjarvis.claw.sentry")
  id("dev.msfjarvis.claw.versioning-plugin")
  alias(libs.plugins.anvil)
  alias(libs.plugins.modulegraphassert)
  alias(libs.plugins.baselineprofile)
  alias(libs.plugins.licensee)
  alias(libs.plugins.ksp)
}

android {
  namespace = "dev.msfjarvis.claw.android"
  defaultConfig.applicationId = "dev.msfjarvis.claw.android"
  buildFeatures.compose = true
  composeOptions {
    useLiveLiterals = false
    kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
  }
  // kotlinx.serialization 1.6.0-RC regression
  packagingOptions.resources.pickFirsts += "**/*.bin"
}

baselineProfile {
  dexLayoutOptimization = true
  mergeIntoMain = true
  saveInSrc = true
  from(projects.benchmark.dependencyProject)
}

licensee {
  allow("Apache-2.0")
  allow("MIT")
  ignoreDependencies("org.commonmark") { because("Commonmark is BSD licensed") }
  allowUrl("https://jsoup.org/license") { because("Jsoup is MIT licensed") }
}

moduleGraphAssert {
  assertOnAnyBuild = true
  maxHeight = 4
  restricted = arrayOf(":core -X> :.*")
}

dependencies {
  anvil(libs.whetstone.compiler)

  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.okhttp.bom))
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.material)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.material3.window.size)
  implementation(libs.androidx.core.splashscreen)
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.paging.compose)
  implementation(libs.androidx.profileinstaller)
  implementation(libs.androidx.work.runtime)
  implementation(libs.coil)
  implementation(libs.copydown)
  implementation(libs.dagger)
  implementation(libs.jsoup)
  implementation(libs.kotlinx.collections.immutable)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.napier)
  implementation(libs.sqldelight.extensions.coroutines)
  implementation(libs.swipe)
  implementation(libs.unfurl)
  implementation(libs.whetstone.compose)
  implementation(libs.whetstone.worker)
  implementation(projects.api)
  implementation(projects.common)
  implementation(projects.core)
  implementation(projects.database)
  implementation(projects.model)

  ksp(libs.dagger.compiler)

  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.okhttp.mockwebserver)
  addTestDependencies(project)
}
