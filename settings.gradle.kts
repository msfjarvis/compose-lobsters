/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("UnstableApiUsage")

pluginManagement {
  repositories {
    exclusiveContent {
      forRepository { google() }
      filter {
        includeGroup("androidx.baselineprofile")
        includeGroup("androidx.benchmark")
        includeGroup("androidx.databinding")
        includeGroupByRegex("com.android.*")
        includeGroup("com.google.testing.platform")
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
        includeModule("com.gradle", "gradle-enterprise-gradle-plugin")
        includeModule("com.gradle.enterprise", "com.gradle.enterprise.gradle.plugin")
        includeModule("com.jraska.module.graph.assertion", "plugin")
        includeModule(
          "org.gradle.toolchains.foojay-resolver-convention",
          "org.gradle.toolchains.foojay-resolver-convention.gradle.plugin",
        )
        includeModule("org.gradle.toolchains", "foojay-resolver")
      }
    }
    includeBuild("build-logic")
    maven("https://oss.sonatype.org/content/repositories/snapshots/") {
      name = "Sonatype Snapshots"
      content { includeGroup("dev.msfjarvis.whetstone") }
      mavenContent { snapshotsOnly() }
    }
    mavenCentral { mavenContent { releasesOnly() } }
  }
}

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
  id("com.gradle.enterprise") version "3.16.1"
}

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = if (System.getenv("GITHUB_WORKFLOW").isNullOrEmpty()) "no" else "yes"
    publishOnFailureIf(!System.getenv("GITHUB_WORKFLOW").isNullOrEmpty())
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google {
      content {
        includeGroupByRegex("androidx.*")
        includeGroupByRegex("com.android.*")
        includeGroup("com.google.android.gms")
        includeModule("com.google.android.material", "material")
      }
    }
    exclusiveContent {
      forRepository { maven("https://jitpack.io") { name = "JitPack" } }
      filter { includeGroup("com.github.requery") }
    }
    maven("https://androidx.dev/storage/compose-compiler/repository") {
      name = "Compose Compiler Snapshots"
      content { includeGroup("androidx.compose.compiler") }
    }
    maven("https://oss.sonatype.org/content/repositories/snapshots/") {
      name = "Sonatype Snapshots"
      content {
        includeGroup("dev.msfjarvis.whetstone")
        includeGroup("me.saket.swipe")
      }
      mavenContent { snapshotsOnly() }
    }
    mavenCentral { mavenContent { releasesOnly() } }
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
  "database:core",
  "database:impl",
  "model",
  "web",
)
