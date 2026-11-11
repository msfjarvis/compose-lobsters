/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
plugins {
  id("dev.msfjarvis.claw.kotlin-multiplatform")
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.dependencyAnalysis)
}

tasks.withType<Test>().configureEach { useJUnitPlatform() }

kotlin {
  jvm()
  js {
    browser()
  }

  sourceSets {
    commonMain {
      dependencies {
        api(libs.kotlinx.datetime)
        api(libs.kotlinx.serialization.core)
      }
    }
    jvmTest {
      dependencies {
        implementation(kotlin("test"))
        implementation(project.dependencies.platform(libs.junit.bom))
        implementation(libs.junit.jupiter.api)
        implementation(libs.truth)
        runtimeOnly(libs.junit.jupiter.engine)
        runtimeOnly(libs.junit.platform.launcher)
        runtimeOnly(libs.junit.legacy)
      }
    }
  }
}
