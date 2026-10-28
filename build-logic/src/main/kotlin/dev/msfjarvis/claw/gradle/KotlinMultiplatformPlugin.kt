/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import com.android.build.api.dsl.Lint
import com.android.build.gradle.LintPlugin
import dev.msfjarvis.claw.gradle.LintConfig.configureLint
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

@Suppress("Unused")
class KotlinMultiplatformPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.pluginManager.apply {
      apply(KotlinMultiplatformPluginWrapper::class)
      apply(LintPlugin::class)
      apply(SpotlessPlugin::class)
    }
    val extension = project.extensions.getByType<KotlinMultiplatformExtension>()
    extension.targets.configureEach {
      if (this is KotlinJvmTarget) {
        project.extensions.getByType<Lint>().configureLint(project, isJVM = true)
      }
    }
  }
}
