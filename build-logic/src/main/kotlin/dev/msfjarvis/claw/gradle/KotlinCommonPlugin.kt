/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("Unused")
class KotlinCommonPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    project.tasks.run {
      withType<KotlinCompile>().configureEach {
        compilerOptions {
          allWarningsAsErrors.set(true)
          freeCompilerArgs.addAll(ADDITIONAL_COMPILER_ARGS)
          languageVersion.set(KotlinVersion.KOTLIN_2_3)
        }
      }
      withType<Test>().configureEach {
        maxParallelForks = Runtime.getRuntime().availableProcessors() * 2
        testLogging { events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED) }
        useJUnitPlatform()
      }
    }
  }

  companion object {
    private val ADDITIONAL_COMPILER_ARGS =
      listOf(
        "-opt-in=kotlin.RequiresOptIn",
        "-Xjspecify-annotations=strict",
        "-Xtype-enhancement-improvements-strict-mode",
        "-Xcontext-parameters",
        // TODO trips in SQLDelight code: https://github.com/sqldelight/sqldelight/issues/6029
        // "-Xreturn-value-checker=full",
        "-Xcontext-sensitive-resolution",
        "-Xdata-flow-based-exhaustiveness",
        "-Xwhen-expressions=indy",
        "-Xexplicit-backing-fields",
      )
  }
}
