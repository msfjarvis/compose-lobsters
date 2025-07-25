/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("UnstableApiUsage")

import com.android.build.api.dsl.ApplicationExtension
import dev.msfjarvis.claw.gradle.addTestDependencies

plugins {
  id("dev.msfjarvis.claw.android-application")
  id("dev.msfjarvis.claw.rename-artifacts")
  id("dev.msfjarvis.claw.kotlin-android")
  id("dev.msfjarvis.claw.kotlin-kapt")
  id("dev.msfjarvis.claw.sentry")
  id("dev.msfjarvis.claw.versioning-plugin")
  id("kotlin-parcelize")
  alias(libs.plugins.aboutlibraries)
  alias(libs.plugins.android.junit5)
  alias(libs.plugins.anvil)
  alias(libs.plugins.modulegraphassert)
  alias(libs.plugins.whetstone)
  alias(libs.plugins.licensee)
  alias(libs.plugins.tracelog)
  alias(libs.plugins.kotlin.composeCompiler)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.dependencyAnalysis)
}

// Directly using the generated `android` accessor lights up bright red
extensions.configure<ApplicationExtension> {
  namespace = "dev.msfjarvis.claw.android"
  defaultConfig.applicationId = "dev.msfjarvis.claw.android"
  defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  buildFeatures.compose = true
  buildTypes.create("internal") {
    matchingFallbacks += "release"
    signingConfig = signingConfigs["debug"]
    applicationIdSuffix = ".internal"
    isDebuggable = true
  }
}

aboutLibraries.collect.gitHubApiToken = providers.environmentVariable("GITHUB_TOKEN").orNull

// TODO: Remove when Nav3 gets a new release
configurations.configureEach {
  resolutionStrategy {
    eachDependency {
      if (requested.group == "androidx.lifecycle") {
        useVersion(libs.versions.lifecycle.get())
      }
    }
  }
}

licensee {
  allow("Apache-2.0")
  allow("MIT")
  allow("BSD-3-Clause")
  ignoreDependencies("com.michael-bull.kotlin-result") { because("kotlin-result is ISC licensed") }
  ignoreDependencies("org.commonmark") { because("Commonmark is BSD licensed") }
  allowUrl("https://jsoup.org/license") { because("Jsoup is MIT licensed") }
}

moduleGraphAssert {
  assertOnAnyBuild = true
  maxHeight = 4
  restricted = arrayOf(":core -X> :.*")
}

whetstone {
  addOns {
    compose.set(true)
    workManager.set(true)
  }
}

dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.okhttp.bom))
  implementation(libs.aboutLibraries.m3)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.animation)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.glance)
  implementation(libs.androidx.compose.glance.m3)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.material3.window.size)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.text)
  implementation(libs.androidx.compose.ui.util)
  implementation(libs.androidx.core.splashscreen)
  implementation(libs.androidx.lifecycle.compose)
  implementation(libs.androidx.material3.navigation3)
  implementation(libs.androidx.navigation3.runtime)
  implementation(libs.androidx.navigation3.ui)
  implementation(libs.androidx.paging.compose)
  implementation(libs.androidx.profileinstaller)
  implementation(libs.androidx.work.runtime)
  implementation(libs.dagger)
  implementation(libs.eithernet)
  implementation(libs.haze)
  implementation(libs.haze.materials)
  implementation(libs.javax.inject)
  implementation(libs.kotlinx.collections.immutable)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.kotlinx.serialization.core)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.napier)
  implementation(libs.okhttp.core)
  implementation(libs.okhttp.loggingInterceptor)
  implementation(libs.retrofit)
  implementation(libs.sqldelight.extensions.coroutines)
  implementation(libs.swipe)
  implementation(libs.unfurl)
  implementation(projects.api)
  implementation(projects.common)
  implementation(projects.core)
  implementation(projects.database.core)
  implementation(projects.database.impl)
  implementation(projects.model)
  implementation(projects.web)

  kapt(libs.dagger.compiler)

  addTestDependencies(project)
  androidTestImplementation(libs.androidx.test.uiautomator)
  androidTestImplementation(libs.leakcanary.android.test)
}
