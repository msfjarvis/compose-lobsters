/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("UnstableApiUsage")

pluginManagement {
  repositories {
    exclusiveContent {
      forRepository { google { mavenContent { releasesOnly() } } }
      filter {
        includeGroup("androidx.annotation")
        includeGroup("androidx.baselineprofile")
        includeGroup("androidx.benchmark")
        includeGroup("androidx.databinding")
        includeGroupAndSubgroups("androidx.navigation")
        includeGroup("com.google.testing.platform")
        includeGroupAndSubgroups("com.android")
      }
    }
    exclusiveContent {
      forRepository { gradlePluginPortal() }
      filter {
        includeModule("com.mikepenz.aboutlibraries.plugin", "aboutlibraries-plugin")
        includeModule(
          "com.mikepenz.aboutlibraries.plugin.android",
          "com.mikepenz.aboutlibraries.plugin.android.gradle.plugin",
        )
        includeModule("com.github.ben-manes", "gradle-versions-plugin")
        includeModule("org.gradle.android.cache-fix", "org.gradle.android.cache-fix.gradle.plugin")
        includeModule("gradle.plugin.org.gradle.android", "android-cache-fix-gradle-plugin")
        includeModule("dev.iurysouza.modulegraph", "dev.iurysouza.modulegraph.gradle.plugin")
        includeModule("dev.iurysouza", "modulegraph")
        includeModule(
          "com.jraska.module.graph.assertion",
          "com.jraska.module.graph.assertion.gradle.plugin",
        )
        includeModule("com.gradle", "develocity-gradle-plugin")
        includeModule("com.gradle.develocity", "com.gradle.develocity.gradle.plugin")
        includeModule("com.jraska.module.graph.assertion", "plugin")
        includeModule(
          "org.gradle.toolchains.foojay-resolver-convention",
          "org.gradle.toolchains.foojay-resolver-convention.gradle.plugin",
        )
        includeModule("org.gradle.toolchains", "foojay-resolver")
      }
    }
    includeBuild("build-logic")
    maven("https://central.sonatype.com/repository/maven-snapshots/") {
      name = "Sonatype Snapshots"
      mavenContent { snapshotsOnly() }
    }
    mavenCentral { mavenContent { releasesOnly() } }
  }
}

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
  id("com.gradle.develocity") version "4.4.3"
}

develocity {
  buildScan {
    termsOfUseUrl = "https://gradle.com/help/legal-terms-of-use"
    termsOfUseAgree = if (System.getenv("GITHUB_WORKFLOW").isNullOrEmpty()) "no" else "yes"
    publishing.onlyIf { !System.getenv("GITHUB_WORKFLOW").isNullOrEmpty() }
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
  repositories {
    google {
      mavenContent { releasesOnly() }
      content {
        includeGroupAndSubgroups("androidx")
        includeGroupAndSubgroups("com.android")
        includeGroup("com.google.android.gms")
        includeModule("com.google.android.material", "material")
        includeGroup("com.google.testing.platform")
      }
    }
    exclusiveContent {
      forRepository {
        maven("https://jitpack.io") {
          name = "JitPack"
          mavenContent { releasesOnly() }
        }
      }
      filter { includeGroup("com.github.requery") }
    }
    maven("https://androidx.dev/storage/compose-compiler/repository") {
      name = "Compose Compiler Snapshots"
      content { includeGroup("androidx.compose.compiler") }
    }
    maven("https://central.sonatype.com/repository/maven-snapshots/") {
      name = "Sonatype Snapshots"
      mavenContent { snapshotsOnly() }
    }
    exclusiveContent {
      forRepository {
        ivy("https://download.jetbrains.com/kotlin/native/builds") {
          name = "Kotlin Native"
          patternLayout {
            listOf(
                "macos-x86_64",
                "macos-aarch64",
                "osx-x86_64",
                "osx-aarch64",
                "linux-x86_64",
                "windows-x86_64",
              )
              .forEach { os ->
                listOf("dev", "releases").forEach { stage ->
                  artifact("$stage/[revision]/$os/[artifact]-[revision].[ext]")
                }
              }
          }
          metadataSources { artifact() }
        }
      }
      filter { includeModuleByRegex(".*", ".*kotlin-native-prebuilt.*") }
    }
    exclusiveContent {
      forRepository {
        ivy("https://nodejs.org/dist/") {
          name = "Node Distributions at $url"
          patternLayout { artifact("v[revision]/[artifact](-v[revision]-[classifier]).[ext]") }
          metadataSources { artifact() }
          content { includeModule("org.nodejs", "node") }
        }
      }
      filter { includeGroup("org.nodejs") }
    }
    exclusiveContent {
      forRepository {
        ivy("https://github.com/yarnpkg/yarn/releases/download") {
          name = "Yarn Distributions at $url"
          patternLayout { artifact("v[revision]/[artifact](-v[revision]).[ext]") }
          metadataSources { artifact() }
          content { includeModule("com.yarnpkg", "yarn") }
        }
      }
      filter { includeGroup("com.yarnpkg") }
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
  "smoke-tests",
  "zipline-parser",
  "zipline-parser-api",
)
