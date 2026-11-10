/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
plugins {
  id("dev.msfjarvis.claw.kotlin-jvm")
  alias(libs.plugins.dependencyAnalysis)
}

dependencies {
  api(platform(libs.okhttp.bom))
  api(libs.eithernet)
  api(libs.kotlinx.coroutines.core)
  api(libs.okhttp.core)
  api(libs.retrofit)
  api(projects.model)
}
