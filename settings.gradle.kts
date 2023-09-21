/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("UnstableApiUsage")

pluginManagement {
  plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0" }
  repositories {
    exclusiveContent {
      forRepository { google() }
      filter {
        includeGroup("androidx.baselineprofile")
        includeGroup("androidx.benchmark")
        includeGroup("androidx.databinding")
        includeGroup("com.android")
        includeModule("com.android.test", "com.android.test.gradle.plugin")
        includeGroup("com.android.tools.analytics-library")
        includeGroup("com.android.tools.build")
        includeGroup("com.android.tools.build.jetifier")
        includeGroup("com.android.databinding")
        includeGroup("com.android.tools.ddms")
        includeGroup("com.android.tools.layoutlib")
        includeGroup("com.android.tools.lint")
        includeGroup("com.android.tools.utp")
        includeGroup("com.google.testing.platform")
        includeModule("com.android.tools", "annotations")
        includeModule("com.android.tools", "common")
        includeModule("com.android.tools", "desugar_jdk_libs")
        includeModule("com.android.tools", "desugar_jdk_libs_configuration")
        includeModule("com.android.tools", "dvlib")
        includeModule("com.android.tools", "play-sdk-proto")
        includeModule("com.android.tools", "repository")
        includeModule("com.android.tools", "sdklib")
        includeModule("com.android.tools", "sdk-common")
      }
    }
    exclusiveContent {
      forRepository { gradlePluginPortal() }
      filter {
        includeModule("com.github.ben-manes", "gradle-versions-plugin")
        includeModule("org.gradle.android.cache-fix", "org.gradle.android.cache-fix.gradle.plugin")
        includeModule("gradle.plugin.org.gradle.android", "android-cache-fix-gradle-plugin")
        includeModule("dev.iurysouza.modulegraph", "dev.iurysouza.modulegraph.gradle.plugin")
        includeModule("dev.iurysouza", "modulegraph")
        includeModule(
          "com.jraska.module.graph.assertion",
          "com.jraska.module.graph.assertion.gradle.plugin"
        )
        includeModule("com.jraska.module.graph.assertion", "plugin")
      }
    }
    exclusiveContent {
      forRepository {
        maven("https://storage.googleapis.com/r8-releases/raw/main") { name = "R8 mainline" }
      }
      filter { includeModule("com.android.tools", "r8") }
    }
    includeBuild("build-logic")
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/") {
      name = "Sonatype Snapshots"
      content { includeGroup("dev.msfjarvis.whetstone") }
    }
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    exclusiveContent {
      forRepository { google() }
      filter {
        includeGroup("androidx.activity")
        includeGroup("androidx.appcompat")
        includeGroup("androidx.annotation")
        includeGroup("androidx.arch.core")
        includeGroup("androidx.autofill")
        includeGroup("androidx.benchmark")
        includeGroup("androidx.browser")
        includeGroup("androidx.collection")
        includeGroup("androidx.compose")
        includeGroup("androidx.compose.animation")
        includeGroup("androidx.compose.foundation")
        includeGroup("androidx.compose.material")
        includeGroup("androidx.compose.material3")
        includeGroup("androidx.compose.runtime")
        includeGroup("androidx.compose.ui")
        includeGroup("androidx.concurrent")
        includeGroup("androidx.core")
        includeGroup("androidx.cursoradapter")
        includeGroup("androidx.customview")
        includeGroup("androidx.databinding")
        includeGroup("androidx.datastore")
        includeGroup("androidx.drawerlayout")
        includeGroup("androidx.emoji2")
        includeGroup("androidx.exifinterface")
        includeGroup("androidx.fragment")
        includeGroup("androidx.interpolator")
        includeGroup("androidx.lifecycle")
        includeGroup("androidx.loader")
        includeGroup("androidx.navigation")
        includeGroup("androidx.paging")
        includeGroup("androidx.profileinstaller")
        includeGroup("androidx.resourceinspection")
        includeGroup("androidx.room")
        includeGroup("androidx.savedstate")
        includeGroup("androidx.startup")
        includeGroup("androidx.sqlite")
        includeGroup("androidx.test")
        includeGroup("androidx.test.espresso")
        includeGroup("androidx.test.ext")
        includeGroup("androidx.test.services")
        includeGroup("androidx.test.uiautomator")
        includeGroup("androidx.tracing")
        includeGroup("androidx.vectordrawable")
        includeGroup("androidx.versionedparcelable")
        includeGroup("androidx.viewpager")
        includeGroup("androidx.window")
        includeGroup("androidx.work")
        includeGroup("com.android")
        includeGroup("com.android.tools")
        includeGroup("com.android.tools.analytics-library")
        includeGroup("com.android.tools.build")
        includeGroup("com.android.tools.ddms")
        includeGroup("com.android.tools.emulator")
        includeGroup("com.android.tools.external.com-intellij")
        includeGroup("com.android.tools.external.org-jetbrains")
        includeGroup("com.android.tools.layoutlib")
        includeGroup("com.android.tools.lint")
        includeGroup("com.android.tools.utp")
        includeGroup("com.google.android.gms")
        includeGroup("com.google.testing.platform")
        includeModule("com.google.android.material", "material")
      }
    }
    exclusiveContent {
      forRepository { maven("https://jitpack.io") { name = "JitPack" } }
      filter { includeGroup("com.github.requery") }
    }
    exclusiveContent {
      forRepository { maven("https://androidx.dev/storage/compose-compiler/repository") }
      filter { includeGroup("androidx.compose.compiler") }
    }
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/") {
      name = "Sonatype Snapshots"
      content { includeGroup("dev.msfjarvis.whetstone") }
    }
  }
}

rootProject.name = "Claw"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
  "android",
  "api",
  "benchmark",
  "common",
  "core",
  "database",
  "model",
)
