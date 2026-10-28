/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("Unused")
class KotlinCommonPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    project.pluginManager.apply(SpotlessPlugin::class)
    project.tasks.run {
      withType<KotlinCompile>().configureEach {
        compilerOptions {
          jvmTarget.set(JvmTarget.JVM_21)
          allWarningsAsErrors.set(true)
          freeCompilerArgs.addAll(ADDITIONAL_COMPILER_ARGS)
          languageVersion.set(KotlinVersion.KOTLIN_2_4)
        }
      }
      withType<JavaCompile>().configureEach {
        sourceCompatibility = JavaVersion.VERSION_21.toString()
        targetCompatibility = JavaVersion.VERSION_21.toString()
      }
      withType<Test>().configureEach {
        maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
        testLogging { events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED) }
        useJUnitPlatform()
      }
    }
  }

  companion object {
    private val ADDITIONAL_COMPILER_ARGS =
      listOf(
        "-opt-in=kotlin.RequiresOptIn",
        "-Xcollection-literals",
        "-Xintrinsic-const-evaluation",
        "-Xjspecify-annotations=strict",
        // TODO trips in SQLDelight code: https://github.com/sqldelight/sqldelight/issues/6029
        // "-Xreturn-value-checker=full",
        "-Xcontext-sensitive-resolution",
        "-Xwhen-expressions=indy",
      )
  }
}
