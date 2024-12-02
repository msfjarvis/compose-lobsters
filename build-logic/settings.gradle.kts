/*
 * Copyright © 2022-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("UnstableApiUsage")

rootProject.name = "build-logic"

pluginManagement {
  plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0" }
  repositories {
    exclusiveContent {
      forRepository { gradlePluginPortal() }
      filter {
        includeModule(
          "org.gradle.toolchains.foojay-resolver-convention",
          "org.gradle.toolchains.foojay-resolver-convention.gradle.plugin",
        )
        includeModule("org.gradle.toolchains", "foojay-resolver")
        includeModule("org.gradle.kotlin.kotlin-dsl", "org.gradle.kotlin.kotlin-dsl.gradle.plugin")
        includeModule("org.gradle.kotlin", "gradle-kotlin-dsl-plugins")
      }
    }
    exclusiveContent {
      forRepository { google() }
      filter {
        includeGroup("androidx.databinding")
        includeGroup("com.google.testing.platform")
        includeGroupAndSubgroups("com.android")
      }
    }
    mavenCentral { mavenContent { releasesOnly() } }
  }
}

dependencyResolutionManagement {
  repositories {
    exclusiveContent {
      forRepository(::google)
      filter {
        includeGroup("androidx.databinding")
        includeGroup("androidx.lint")
        includeGroupByRegex("com.android.*")
        includeGroup("com.google.testing.platform")
      }
    }
    exclusiveContent {
      forRepository(::gradlePluginPortal)
      filter {
        includeModule("com.github.ben-manes", "gradle-versions-plugin")
        includeModule("org.gradle.android.cache-fix", "org.gradle.android.cache-fix.gradle.plugin")
        includeModule("gradle.plugin.org.gradle.android", "android-cache-fix-gradle-plugin")
      }
    }
    mavenCentral { mavenContent { releasesOnly() } }
  }
  versionCatalogs { create("libs") { from(files("../gradle/libs.versions.toml")) } }
}
