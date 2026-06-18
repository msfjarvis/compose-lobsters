/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
plugins {
  application
  id("dev.msfjarvis.claw.kotlin-jvm")
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.metro)
}

application {
  mainClass.set("dev.msfjarvis.claw.smoketests.MainKt")
}

dependencies {
  implementation(projects.api)
  implementation(projects.core)
  implementation(projects.ziplineParser)
  implementation(libs.eithernet.integration.retrofit)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.okhttp.core)
  implementation(libs.retrofit)
}
