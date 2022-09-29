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
        includeGroup("androidx.activity")
        includeGroup("androidx.appcompat")
        includeGroup("androidx.annotation")
        includeGroup("androidx.arch.core")
        includeGroup("androidx.autofill")
        includeGroup("androidx.benchmark")
        includeGroup("androidx.browser")
        includeGroup("androidx.collection")
        includeGroup("androidx.compose.animation")
        includeGroup("androidx.compose.compiler")
        includeGroup("androidx.compose.foundation")
        includeGroup("androidx.compose.material")
        includeGroup("androidx.compose.material3")
        includeGroup("androidx.compose.runtime")
        includeGroup("androidx.compose.ui")
        includeGroup("androidx.concurrent")
        includeGroup("androidx.core")
        includeGroup("androidx.customview")
        includeGroup("androidx.databinding")
        includeGroup("androidx.exifinterface")
        includeGroup("androidx.fragment")
        includeGroup("androidx.hilt")
        includeGroup("androidx.interpolator")
        includeGroup("androidx.lifecycle")
        includeGroup("androidx.loader")
        includeGroup("androidx.navigation")
        includeGroup("androidx.paging")
        includeGroup("androidx.profileinstaller")
        includeGroup("androidx.room")
        includeGroup("androidx.savedstate")
        includeGroup("androidx.startup")
        includeGroup("androidx.sqlite")
        includeGroup("androidx.test")
        includeGroup("androidx.tracing")
        includeGroup("androidx.vectordrawable")
        includeGroup("androidx.versionedparcelable")
        includeGroup("androidx.viewpager")
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
