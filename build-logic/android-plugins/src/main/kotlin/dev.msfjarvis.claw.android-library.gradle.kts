/*
 * Copyright © 2014-2021 The Android Password Store Authors. All Rights Reserved.
 * SPDX-License-Identifier: GPL-3.0-only
 */

plugins {
  id("com.android.library")
  id("dev.msfjarvis.claw.android-common")
}

android { defaultConfig { consumerProguardFiles("consumer-rules.pro") } }
