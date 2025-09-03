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
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmExtension
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
    project.extensions.getByType<KotlinJvmExtension>().compilerOptions {
      jvmTarget.set(JvmTarget.JVM_21)
    }
    project.tasks.withType<JavaCompile>().configureEach {
      sourceCompatibility = JavaVersion.VERSION_21.toString()
      targetCompatibility = JavaVersion.VERSION_21.toString()
    }
  }
}
