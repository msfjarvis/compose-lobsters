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
  id("dev.msfjarvis.claw.android-distribution")
  id("dev.msfjarvis.claw.android-test-devices")
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
  alias(libs.plugins.zipline)
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
  restricted =
    arrayOf(
      ":core:.* -X> :feature:.*",
      ":core:.* -X> :android",
      ":feature:.*:api -X> :feature:.*",
      ":feature:.*:impl -X> :feature:.*:impl",
    )
}

dependencies {
  baselineProfile(projects.benchmark)
  add("embeddedZiplineParser", projects.ziplineParser)

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
  implementation(libs.androidx.browser)
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
  implementation(libs.coil3.network.okhttp)
  implementation(libs.eithernet)
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
  implementation(libs.okhttp.loggingInterceptor)
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
  implementation(projects.core.network.api)
  implementation(projects.core.network.impl)
  implementation(projects.core.parser.api)
  implementation(projects.core.parser.impl)
  implementation(projects.core.work.api)
  implementation(projects.core.work.impl)
  implementation(projects.core.common)
  implementation(projects.core.navigation.api)
  implementation(projects.feature.posts.api)
  implementation(projects.feature.posts.impl)
  implementation(projects.feature.discussion.impl)
  implementation(projects.feature.profile.impl)
  implementation(projects.feature.auth.impl)
  implementation(projects.feature.tags.impl)
  implementation(projects.feature.settings.impl)
  implementation(projects.feature.search.api)
  implementation(projects.feature.search.impl)
  implementation(projects.feature.reminders.api)
  implementation(projects.feature.reminders.impl)
  implementation(projects.feature.widgets.api)
  implementation(projects.feature.widgets.impl)
  implementation(projects.core.navigation.impl)
  implementation(projects.core.database.api)
  implementation(projects.core.database.impl)
  implementation(projects.core.ui)
  implementation(projects.model)

  runtimeOnly(libs.androidx.profileinstaller)

  screenshotTestImplementation(libs.screenshot.validation.api)
  screenshotTestImplementation(libs.androidx.compose.ui.tooling)

  testImplementation(libs.kotlinx.coroutines.test)

  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.rules)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)

  addTestDependencies(project)
}
