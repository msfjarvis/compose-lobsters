/*
 * Copyright © 2014-2021 The Android Password Store Authors. All Rights Reserved.
 * SPDX-License-Identifier: GPL-3.0-only
 */
@file:Suppress("UnstableApiUsage")

rootProject.name = "build-logic"

dependencyResolutionManagement {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal() {
      content { includeModule("com.github.ben-manes", "gradle-versions-plugin") }
    }
  }
  versionCatalogs { create("libs") { from(files("../gradle/libs.versions.toml")) } }
}

include("android-plugins")

include("kotlin-plugins")
