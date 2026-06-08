/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
  kotlin("multiplatform")
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.dependencyAnalysis)
}

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
    jvmMain {
      dependencies {
        api(projects.database.core)
      }
    }
    jvmTest {
      dependencies {
        implementation(kotlin("test"))
      }
    }
  }
}

@Suppress("UnstableApiUsage")
tasks.withType(KotlinCompilationTask::class.java).configureEach {
  compilerOptions.freeCompilerArgs.add("-Xskip-prerelease-check")
}
