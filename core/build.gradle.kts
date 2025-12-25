/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
plugins {
  id("dev.msfjarvis.claw.android-library")
  id("dev.msfjarvis.claw.kotlin-android")
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.metro)
}

android { namespace = "dev.msfjarvis.claw.core" }

dependencies {
  api(libs.kotlinx.coroutines.core)
  api(libs.kotlinx.serialization.json)
  api(libs.okhttp.loggingInterceptor)
  api(libs.napier)
  api(libs.okhttp.core)
  api(libs.retrofit)
  api(libs.metrox.android)
  api(libs.metrox.viewmodel)
  api(libs.androidx.lifecycle.viewmodel)
  api(libs.androidx.work.runtime)

  implementation(platform(libs.okhttp.bom))
  implementation(libs.kotlinx.serialization.core)
  implementation(libs.retrofit.kotlinxSerializationConverter)
}
