/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

// Forced to specify these here to prevent the classic
// classloader nonsense.
buildscript {
  dependencies {
    classpath(libs.build.kotlin.gradle)
    classpath(libs.build.spotless)
  }
}

plugins {
  alias(libs.plugins.android.test) apply false
  alias(libs.plugins.dependencyAnalysis)
}
