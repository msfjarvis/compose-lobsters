/*
 * Copyright © 2021-2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

plugins {
  id("dev.msfjarvis.claw.kotlin-jvm")
  alias(libs.plugins.kotlin.serialization)
}

dependencies { implementation(libs.kotlinx.serialization.core) }
