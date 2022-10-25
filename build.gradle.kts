/*
 * Copyright © 2021-2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  id("dev.msfjarvis.claw.spotless")
  id("dev.msfjarvis.claw.versions")
  id("dev.msfjarvis.claw.kotlin-common")
  alias(libs.plugins.hilt) apply false
  alias(libs.plugins.android.test) apply false
}
