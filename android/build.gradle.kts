/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("UnstableApiUsage")

import com.android.build.api.variant.HasUnitTestBuilder
import dev.msfjarvis.claw.gradle.addTestDependencies
import dev.zacsweers.metro.gradle.DiagnosticSeverity

plugins {
  id("dev.msfjarvis.claw.android-application")
  id("dev.msfjarvis.claw.rename-artifacts")
  id("dev.msfjarvis.claw.kotlin-android")
  id("dev.msfjarvis.claw.sentry")
  id("dev.msfjarvis.claw.versioning-plugin")
  id("kotlin-parcelize")
  alias(libs.plugins.android.junit5)
  alias(libs.plugins.aboutlibraries)
  alias(libs.plugins.modulegraphassert)
  alias(libs.plugins.baselineprofile)
  alias(libs.plugins.licensee)
  alias(libs.plugins.screenshot)
  alias(libs.plugins.kotlin.composeCompiler)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.metro)
  id("dev.msfjarvis.claw.zipline-setup")
}

zipline {
  ziplineApiVersion.set("v3")
  devBuildTypes.set(listOf("debug"))
  enforceSignatureCheckBuildTypes.set(listOf("release"))
}

// Compose Preview runfiles are generated from the unit-test component for each app variant.
androidComponents {
  beforeVariants { variant ->
    (variant as HasUnitTestBuilder).enableUnitTest = true
  }
}

android {
  namespace = "dev.msfjarvis.claw.android"
  defaultConfig.applicationId = "dev.msfjarvis.claw.android"
  defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  buildFeatures.compose = true
  experimentalProperties["android.experimental.enableScreenshotTest"] = true

  flavorDimensions += "distribution"
  productFlavors {
    create("foss") { dimension = "distribution" }
    create("gplay") { dimension = "distribution" }
  }

  buildTypes {
    create("internal") {
      matchingFallbacks += "release"
      signingConfig = signingConfigs["debug"]
      applicationIdSuffix = ".internal"
      isDebuggable = true
    }
  }

  testOptions {
    // Compose Preview runfiles need Android resources on the unit-test runtime classpath.
    unitTests.isIncludeAndroidResources = true
    managedDevices {
      localDevices {
        create("pixel9api36") {
          device = "Pixel 9"
          apiLevel = 36
          systemImageSource = "aosp"
        }
      }
    }
  }
}

aboutLibraries.collect.gitHubApiToken = providers.environmentVariable("GITHUB_TOKEN").orNull

baselineProfile {
  mergeIntoMain = true
  saveInSrc = true
}

licensee {
  allow("Apache-2.0")
  allow("MIT")
  allow("BSD-3-Clause")
  ignoreDependencies("com.michael-bull.kotlin-result") { because("kotlin-result is ISC licensed") }
  ignoreDependencies("org.commonmark") { because("Commonmark is BSD licensed") }
  allowUrl("https://jsoup.org/license") { because("Jsoup is MIT licensed") }
  allowUrl("https://opensource.org/license/MIT") { because("Ksoup is MIT licensed") }
  allowUrl("https://developer.android.com/guide/playcore/license") {
    because("Play Core is licensed under the Play Core SDK terms of service")
  }
  allowUrl("https://developer.android.com/studio/terms.html") {
    because("Google Play services is licensed under the Android SDK terms of service")
  }
  allowUrl("https://github.com/Osmerion/sqlite-android/blob/master/LICENSE") {
    because("sqlite-android is MIT licensed")
  }
}

metro { unusedGraphInputsSeverity = DiagnosticSeverity.ERROR }

moduleGraphAssert {
  assertOnAnyBuild = true
  maxHeight = 4
  restricted = arrayOf(":core -X> :.*")
}

dependencies {
  baselineProfile(projects.benchmark)

  implementation(platform(libs.okhttp.bom))
  implementation(libs.aboutLibraries.compose.core)
  implementation(libs.aboutLibraries.core)
  implementation(libs.aboutLibraries.m3)
  implementation(libs.androidx.activity)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.animation)
  implementation(libs.androidx.compose.animation.core)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.foundation.layout)
  implementation(libs.androidx.compose.glance)
  implementation(libs.androidx.compose.glance.core)
  implementation(libs.androidx.compose.glance.m3)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.material3.adaptive)
  implementation(libs.androidx.compose.material3.adaptive.layout)
  implementation(libs.androidx.compose.material3.window.size)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.runtime.saveable)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.text)
  implementation(libs.androidx.compose.ui.unit)
  implementation(libs.androidx.annotation)
  implementation(libs.androidx.collection)
  implementation(libs.androidx.core)
  implementation(libs.androidx.core.splashscreen)
  implementation(libs.androidx.lifecycle.common)
  implementation(libs.androidx.lifecycle.compose)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.viewmodel)
  implementation(libs.androidx.material3.navigation3)
  implementation(libs.androidx.navigation3.runtime)
  implementation(libs.androidx.navigation3.ui)
  implementation(libs.androidx.paging.common)
  implementation(libs.androidx.paging.compose)
  implementation(libs.androidx.work.runtime)
  implementation(libs.coil3.compose)
  implementation(libs.coil3.core)
  implementation(libs.eithernet)
  implementation(libs.haze)
  implementation(libs.haze.blur)
  implementation(libs.kotlin.parcelize.runtime)
  implementation(libs.kotlinx.collections.immutable)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.kotlinx.datetime)
  implementation(libs.kotlinx.serialization.core)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.kotlin.stdlib)
  implementation(libs.metrox.android)
  implementation(libs.metrox.viewmodel)
  implementation(libs.metrox.viewmodel.compose)
  implementation(libs.okhttp.core)
  add("gplayImplementation", libs.play.app.update)
  implementation(libs.retrofit)
  implementation(libs.sentry)
  implementation(libs.sentry.android.core)
  implementation(libs.sentry.android.navigation3)
  implementation(libs.sqldelight.extensions.coroutines)
  implementation(libs.sqldelight.runtime)
  implementation(libs.swipe)
  implementation(libs.unfurl)
  implementation(libs.zipline)
  implementation(libs.zipline.loader)
  implementation(projects.api)
  implementation(projects.common)
  implementation(projects.core)
  implementation(projects.database.core)
  implementation(projects.database.impl)
  implementation(projects.model)

  compileOnly(libs.androidx.compose.glance.preview)

  runtimeOnly(libs.androidx.profileinstaller)

  screenshotTestImplementation(libs.screenshot.validation.api)
  screenshotTestImplementation(libs.androidx.compose.ui.tooling)

  testImplementation(libs.kotlinx.coroutines.test)

  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.rules)
  androidTestImplementation(libs.androidx.test.runner)

  addTestDependencies(project)
}
