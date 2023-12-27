/*
 * Copyright © 2022-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("UnstableApiUsage")

rootProject.name = "build-logic"

dependencyResolutionManagement {
  repositories {
    exclusiveContent {
      forRepository(::google)
      filter {
        includeGroup("androidx.databinding")
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
