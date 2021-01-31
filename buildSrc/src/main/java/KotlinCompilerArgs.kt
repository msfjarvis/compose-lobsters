/*
 * Copyright © 2014-2020 The Android Password Store Authors. All Rights Reserved.
 * SPDX-License-Identifier: GPL-3.0-only
 */

internal val additionalCompilerArgs = listOf(
  "-Xallow-jvm-ir-dependencies",
  "-Xopt-in=kotlin.RequiresOptIn",
  "-Xskip-prerelease-check"
)
