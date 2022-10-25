/*
 * Copyright © 2021-2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

plugins {
  kotlin("jvm")
  kotlin("plugin.serialization")
  id("dev.msfjarvis.claw.kotlin-common")
}

dependencies { implementation(libs.kotlinx.serialization.core) }
