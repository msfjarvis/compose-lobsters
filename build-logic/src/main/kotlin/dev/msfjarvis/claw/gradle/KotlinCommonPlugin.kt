/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainSpec
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("Unused", "UnstableApiUsage")
class KotlinCommonPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    if (project.isolated.rootProject == project.isolated) {
      LintConfig.configureRootProject(project)
    } else {
      LintConfig.configureSubProject(project)
    }
    project.tasks.run {
      withType<KotlinCompile>().configureEach {
        compilerOptions {
          // This can't be enabled until we get rid of Anvil because it enables
          // the deprecated Kotlin 1.9 language version.
          allWarningsAsErrors.set(false)
          freeCompilerArgs.addAll(ADDITIONAL_COMPILER_ARGS)
          // This is necessary for Anvil to function, do not be the idiot reverting this
          // 3 months later because you forgot about it.
          languageVersion.set(KotlinVersion.KOTLIN_1_9)
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
      )

    val JVM_TOOLCHAIN_ACTION =
      Action<JavaToolchainSpec> { languageVersion.set(JavaLanguageVersion.of(17)) }
  }
}
