/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
plugins {
  java
  id("dev.msfjarvis.claw.kotlin-jvm")
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.metro)
}

val smokeTestsMainClass = "dev.msfjarvis.claw.smoketests.MainKt"

tasks.register<JavaExec>("run") {
  group = "application"
  description = "Runs the smoke tests application."
  classpath = sourceSets.main.get().runtimeClasspath
  mainClass.set(smokeTestsMainClass)
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
