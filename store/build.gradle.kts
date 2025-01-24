/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
import dev.msfjarvis.claw.gradle.addTestDependencies

plugins {
  id("dev.msfjarvis.claw.android-library")
  id("dev.msfjarvis.claw.kotlin-android")
  alias(libs.plugins.anvil)
  alias(libs.plugins.whetstone)
  alias(libs.plugins.dependencyAnalysis)
}

android { namespace = "dev.msfjarvis.claw.data.store" }

anvil { generateDaggerFactories.set(true) }

dependencies {
  implementation(libs.store)

  addTestDependencies(project)
}
