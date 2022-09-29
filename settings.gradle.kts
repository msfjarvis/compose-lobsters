@file:Suppress("UnstableApiUsage")

pluginManagement {
  repositories {
    exclusiveContent {
      forRepository { google() }
      filter {
        includeGroup("androidx.databinding")
        includeGroup("com.android")
        includeGroup("com.android.tools")
        includeGroup("com.android.tools.analytics-library")
        includeGroup("com.android.tools.build")
        includeGroup("com.android.tools.build.jetifier")
        includeGroup("com.android.databinding")
        includeGroup("com.android.tools.ddms")
        includeGroup("com.android.tools.layoutlib")
        includeGroup("com.android.tools.lint")
        includeGroup("com.android.tools.utp")
        includeGroup("com.google.testing.platform")
        includeModule("com.android.test", "com.android.test.gradle.plugin")
      }
    }
    exclusiveContent {
      forRepository { maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
      filter {
        includeGroupByRegex("org\\.jetbrains\\.compose.*")
        includeGroup("org.jetbrains.skiko")
      }
    }
    exclusiveContent {
      forRepository { gradlePluginPortal() }
      filter {
        includeModule("com.github.ben-manes", "gradle-versions-plugin")
        includeModule("org.gradle.android.cache-fix", "org.gradle.android.cache-fix.gradle.plugin")
        includeModule("gradle.plugin.org.gradle.android", "android-cache-fix-gradle-plugin")
        includeModule("com.sergei-lapin.napt", "com.sergei-lapin.napt.gradle.plugin")
        includeModule("com.sergei-lapin.napt", "gradle")
      }
    }
    includeBuild("build-logic")
    mavenCentral()
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    exclusiveContent {
      forRepository { google() }
      filter {
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
        includeGroupByRegex("androidx.*")
      }
    }
    exclusiveContent {
      forRepository { maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
      filter {
        includeGroupByRegex("org\\.jetbrains\\.compose.*")
        includeGroup("org.jetbrains.skiko")
      }
    }
    exclusiveContent {
      forRepository { gradlePluginPortal() }
      filter {
        includeModule("org.gradle.android.cache-fix", "org.gradle.android.cache-fix.gradle.plugin")
        includeModule("gradle.plugin.org.gradle.android", "android-cache-fix-gradle-plugin")
      }
    }
    mavenCentral()
  }
}

rootProject.name = "Claw"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
  "android",
  "api",
  "benchmark",
  "common",
  "coroutine-utils",
  "database",
  "metadata-extractor",
  "model",
)
