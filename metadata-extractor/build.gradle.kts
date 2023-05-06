/*
 * Copyright © 2022-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
plugins { id("dev.msfjarvis.claw.kotlin-jvm") }

dependencies {
  api(libs.crux)
  api(libs.javax.inject)
  api(libs.okhttp.core)
  api(projects.model)

  implementation(platform(libs.okhttp.bom))
  implementation(libs.jsoup)
}
