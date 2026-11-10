/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
plugins {
  id("dev.msfjarvis.claw.kotlin-jvm")
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.metro)
}

dependencies {
  api(libs.kotlinx.coroutines.core)
}
