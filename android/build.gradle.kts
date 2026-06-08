/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("UnstableApiUsage")

import dev.msfjarvis.claw.gradle.addTestDependencies
import dev.zacsweers.metro.gradle.DiagnosticSeverity
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import java.io.File

plugins {
  id("dev.msfjarvis.claw.android-application")
  id("dev.msfjarvis.claw.rename-artifacts")
  id("dev.msfjarvis.claw.kotlin-android")
  id("dev.msfjarvis.claw.sentry")
  id("dev.msfjarvis.claw.versioning-plugin")
  id("kotlin-parcelize")
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
}

val ziplineDevManifestUrl =
  providers
    .gradleProperty("claw.ziplineParserManifestUrl")
    .orElse("http://10.0.2.2:8080/manifest.zipline.json")
    .get()
val ziplineProdManifestUrl = "https://claw.msfjarvis.dev/current/manifest.zipline.json"
val embeddedZiplineAssetsRoot = layout.buildDirectory.dir("generated/ziplineEmbeddedAssets")
val embeddedZiplineAssetsRootFile =
  layout.buildDirectory.get().dir("generated/ziplineEmbeddedAssets").asFile
val prepareEmbeddedZiplineParser =
  tasks.register("prepareEmbeddedZiplineParser") {
    val sourceDir =
      rootProject.layout.projectDirectory.dir("zipline-parser/build/zipline/Production")
    val outputRoot = embeddedZiplineAssetsRoot
    inputs.dir(sourceDir)
    outputs.dir(outputRoot)
    dependsOn(":zipline-parser:compileProductionExecutableKotlinJsZipline")
    description = "Embed compiled Zipline artifacts into the app"

    doLast {
      val outputDir = outputRoot.get().asFile
      val ziplineDir = File(outputDir, "zipline")
      ziplineDir.deleteRecursively()
      ziplineDir.mkdirs()

      val sourceManifestFile = File(sourceDir.asFile, "manifest.zipline.json")
      check(sourceManifestFile.exists()) { "Missing Zipline manifest: $sourceManifestFile" }

      @Suppress("UNCHECKED_CAST")
      val parsed = JsonSlurper().parse(sourceManifestFile) as MutableMap<String, Any?>
      val modules = parsed["modules"] as? Map<*, *>
      check(!modules.isNullOrEmpty()) {
        "Zipline manifest contains no modules: $sourceManifestFile"
      }

      for ((_, moduleValue) in modules) {
        val module = moduleValue as Map<*, *>
        val sourceName = module["url"] as String
        val targetName = module["sha256"] as String
        val sourceFile = File(sourceDir.asFile, sourceName)
        check(sourceFile.exists()) { "Missing Zipline module: $sourceFile" }
        sourceFile.copyTo(File(ziplineDir, targetName), overwrite = true)
      }

      val unsigned =
        ((parsed["unsigned"] as? Map<*, *>)?.toMutableMap() ?: mutableMapOf()).apply {
          put("freshAtEpochMs", System.currentTimeMillis())
        }
      parsed["unsigned"] = unsigned

      val embeddedManifestFile = File(ziplineDir, "zipline-parser.manifest.zipline.json")
      embeddedManifestFile.writeText(JsonOutput.toJson(parsed))
    }
  }

android {
  namespace = "dev.msfjarvis.claw.android"
  defaultConfig.applicationId = "dev.msfjarvis.claw.android"
  defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  buildFeatures.compose = true
  experimentalProperties["android.experimental.enableScreenshotTest"] = true

  sourceSets.getByName("main").assets.directories.add(embeddedZiplineAssetsRootFile.path)

  buildTypes {
    getByName("debug") {
      buildConfigField(
        "String",
        "ZIPLINE_PARSER_MANIFEST_URL",
        "\"$ziplineDevManifestUrl\"",
      )
      buildConfigField("boolean", "ZIPLINE_PARSER_VERIFY_SIGNATURES", "false")
    }
    getByName("release") {
      buildConfigField(
        "String",
        "ZIPLINE_PARSER_MANIFEST_URL",
        "\"$ziplineProdManifestUrl\"",
      )
      buildConfigField("boolean", "ZIPLINE_PARSER_VERIFY_SIGNATURES", "true")
    }
    create("internal") {
      matchingFallbacks += "release"
      signingConfig = signingConfigs["debug"]
      applicationIdSuffix = ".internal"
      isDebuggable = true
      buildConfigField(
        "String",
        "ZIPLINE_PARSER_MANIFEST_URL",
        "\"$ziplineProdManifestUrl\"",
      )
      buildConfigField("boolean", "ZIPLINE_PARSER_VERIFY_SIGNATURES", "true")
    }
  }
}

tasks.named("preBuild").configure { dependsOn(prepareEmbeddedZiplineParser) }

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
  implementation(libs.retrofit)
  implementation(libs.sentry)
  implementation(libs.sentry.android.core)
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

  debugRuntimeOnly(libs.androidx.compose.glance)

  runtimeOnly(libs.androidx.profileinstaller)

  screenshotTestImplementation(libs.screenshot.validation.api)
  screenshotTestImplementation(libs.androidx.compose.ui.tooling)

  addTestDependencies(project)
}
