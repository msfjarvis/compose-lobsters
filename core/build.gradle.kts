/*
 * Copyright © 2022-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
plugins {
  id("dev.msfjarvis.claw.android-library")
  id("dev.msfjarvis.claw.kotlin-android")
  alias(libs.plugins.anvil)
  alias(libs.plugins.whetstone)
}

android { namespace = "dev.msfjarvis.claw.core" }

anvil { generateDaggerFactories.set(true) }

dependencies {
  api(libs.javax.inject)
  api(libs.kotlinx.coroutines.core)
  api(libs.kotlinx.serialization.json)
  api(libs.okhttp.loggingInterceptor)

  implementation(platform(libs.okhttp.bom))
  implementation(libs.dagger)
  implementation(libs.napier)
  implementation(libs.okhttp.core)
  implementation(libs.retrofit.kotlinxSerializationConverter)
}
