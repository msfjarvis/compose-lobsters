/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

plugins {
  application
  kotlin("jvm")
  alias(libs.plugins.kotlin.serialization)
  id("dev.msfjarvis.claw.kotlin-common")
  alias(libs.plugins.ktor)
}

group = "dev.msfjarvis.claw"

version = "0.0.1"

application {
  mainClass.set("dev.msfjarvis.claw.sync.ApplicationKt")

  val isDevelopment: Boolean = providers.gradleProperty("development").isPresent
  applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
  implementation(libs.ktor.serialization.kotlinx.json.jvm)
  implementation(libs.ktor.server.auth.jvm)
  implementation(libs.ktor.server.call.id.jvm)
  implementation(libs.ktor.server.call.logging.jvm)
  implementation(libs.ktor.server.content.negotiation.jvm)
  implementation(libs.ktor.server.core.jvm)
  implementation(libs.ktor.server.host.common.jvm)
  implementation(libs.ktor.server.hsts.jvm)
  implementation(libs.ktor.server.http.redirect.jvm)
  implementation(libs.ktor.server.locations.jvm)
  implementation(libs.ktor.server.netty.jvm)
  implementation(libs.ktor.server.status.pages.jvm)
  implementation(libs.ktor.server.tests.jvm)
  implementation(libs.logback.classic)
  testImplementation(libs.kotest.assertions.core)
  testImplementation(libs.kotest.runner.junit5)
}
