/*
 * Copyright © 2014-2021 The Android Password Store Authors. All Rights Reserved.
 * SPDX-License-Identifier: GPL-3.0-only
 */
@file:Suppress("UnstableApiUsage")

import signing.configureBuildSigning

plugins {
  id("com.android.application")
  id("dev.msfjarvis.claw.android-common")
}

android {
  adbOptions.installOptions("--user 0")

  dependenciesInfo {
    includeInBundle = false
    includeInApk = false
  }

  buildFeatures { buildConfig = true }

  buildTypes {
    named("release") {
      setProguardFiles(
        listOf(
          "proguard-android-optimize.txt",
          "proguard-rules.pro",
          "proguard-rules-missing-classes.pro",
        )
      )
    }
    named("debug") {
      applicationIdSuffix = ".debug"
      versionNameSuffix = "-debug"
      isMinifyEnabled = false
    }
  }
  project.configureBuildSigning()
}
