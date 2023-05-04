/*
 * Copyright © 2022-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import com.squareup.sort.SortDependenciesPlugin
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
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

@Suppress("Unused")
class KotlinCommonPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    if (project.rootProject == project) {
      LintConfig.configureRootProject(project)
    } else if (project.name != "benchmark") {
      LintConfig.configureSubProject(project)
    }
    project.pluginManager.apply(SortDependenciesPlugin::class)
    project.tasks.run {
      withType<JavaCompile>().configureEach {
        sourceCompatibility = JavaVersion.VERSION_11.toString()
        targetCompatibility = JavaVersion.VERSION_11.toString()
      }
      withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
          allWarningsAsErrors.set(
            project.providers.environmentVariable("GITHUB_WORKFLOW").isPresent
          )
          jvmTarget.set(JvmTarget.JVM_11)
          freeCompilerArgs.addAll(ADDITIONAL_COMPILER_ARGS)
          languageVersion.set(KotlinVersion.KOTLIN_1_7)
        }
      }
      withType<Test>().configureEach {
        maxParallelForks = Runtime.getRuntime().availableProcessors() * 2
        testLogging { events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED) }
        useJUnitPlatform()
      }
    }
  }

  private companion object {
    private val ADDITIONAL_COMPILER_ARGS =
      listOf(
        "-opt-in=kotlin.RequiresOptIn",
      )
  }
}
