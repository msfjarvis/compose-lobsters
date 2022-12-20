/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.internal.Kapt3GradleSubplugin
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper

@Suppress("Unused")
class KotlinKaptPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    project.pluginManager.run {
      apply(KotlinAndroidPluginWrapper::class)
      apply(Kapt3GradleSubplugin::class)
    }
    project.afterEvaluate {
      project.extensions.configure<KaptExtension> {
        javacOptions {
          if (hasDaggerCompilerDependency()) {
            // https://dagger.dev/dev-guide/compiler-options#fastinit-mode
            option("-Adagger.fastInit=enabled")
            // Enable the better, experimental error messages
            // https://github.com/google/dagger/commit/0d2505a727b54f47b8677f42dd4fc5c1924e37f5
            option("-Adagger.experimentalDaggerErrorMessages=enabled")
            // KAPT nests errors causing real issues to be suppressed in CI logs
            option("-Xmaxerrs", 500)
            // Enables per-module validation for faster error detection
            // https://github.com/google/dagger/commit/325b516ac6a53d3fc973d247b5231fafda9870a2
            option("-Adagger.moduleBindingValidation=ERROR")
          }
        }
      }
    }
    project.tasks
      .matching { it.name.startsWith("kapt") && it.name.endsWith("UnitTestKotlin") }
      .configureEach { enabled = false }
  }

  private fun Project.hasDaggerCompilerDependency(): Boolean {
    return configurations.any {
      it.dependencies.any { dependency -> dependency.name == "dagger-compiler" }
    }
  }
}
