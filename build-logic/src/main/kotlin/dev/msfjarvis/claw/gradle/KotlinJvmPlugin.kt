/*
 * Copyright © 2022-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import com.android.build.api.dsl.Lint
import com.android.build.gradle.LintPlugin
import dev.msfjarvis.claw.gradle.KotlinCommonPlugin.Companion.JVM_TOOLCHAIN_ACTION
import dev.msfjarvis.claw.gradle.LintConfig.configureLint
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

@Suppress("Unused")
class KotlinJvmPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.pluginManager.run {
      apply(KotlinPluginWrapper::class)
      apply(LintPlugin::class)
      apply(KotlinCommonPlugin::class)
    }
    project.extensions.findByType<Lint>()?.configureLint(project, isJVM = true)
    project.extensions.getByType<KotlinProjectExtension>().jvmToolchain(JVM_TOOLCHAIN_ACTION)
  }
}
