/*
 * Copyright © 2014-2021 The Android Password Store Authors. All Rights Reserved.
 * SPDX-License-Identifier: GPL-3.0-only
 */

package dev.msfjarvis.aps.gradle

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("Unused")
class KotlinCommonPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    project.tasks.run {
      withType<JavaCompile>().configureEach {
        sourceCompatibility = JavaVersion.VERSION_11.toString()
        targetCompatibility = JavaVersion.VERSION_11.toString()
      }
      withType<KotlinCompile>().configureEach {
        kotlinOptions {
          allWarningsAsErrors = false
          jvmTarget = JavaVersion.VERSION_11.toString()
          freeCompilerArgs = freeCompilerArgs + ADDITIONAL_COMPILER_ARGS
          languageVersion = "1.5"
        }
      }
      withType<Test>().configureEach {
        maxParallelForks = Runtime.getRuntime().availableProcessors() * 2
        testLogging { events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED) }
        doNotTrackState("We want tests to always run even if Gradle thinks otherwise")
      }
    }
  }

  private companion object {
    private val ADDITIONAL_COMPILER_ARGS =
      listOf(
        "-opt-in=kotlin.RequiresOptIn",
        "-P",
        "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=1.7.20",
      )
  }
}
