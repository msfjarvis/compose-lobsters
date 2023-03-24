/*
 * Copyright © 2022-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
plugins {
  id("dev.msfjarvis.claw.kotlin-android")
  id("dev.msfjarvis.claw.android-library")
  alias(libs.plugins.anvil)
  alias(libs.plugins.whetstone)
}

anvil { generateDaggerFactories.set(true) }

android { namespace = "dev.msfjarvis.claw.core" }

dependencies {
  implementation(platform(libs.okhttp.bom))
  api(libs.kotlinx.serialization.json)
  api(libs.okhttp.loggingInterceptor)
  implementation(libs.dagger)
  implementation(libs.javax.inject)
  implementation(libs.napier)
  implementation(libs.okhttp.core)
  implementation(libs.retrofit.kotlinxSerializationConverter)
}
