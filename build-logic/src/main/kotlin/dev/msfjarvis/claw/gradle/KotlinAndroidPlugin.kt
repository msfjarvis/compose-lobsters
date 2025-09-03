/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper

@Suppress("Unused")
class KotlinAndroidPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    project.pluginManager.run {
      apply(KotlinAndroidPluginWrapper::class)
      apply(KotlinCommonPlugin::class)
    }
    project.extensions.getByType<KotlinAndroidExtension>().compilerOptions {
      jvmTarget.set(JvmTarget.JVM_21)
    }
    project.extensions.findByType<LibraryExtension>()?.compileOptions {
      sourceCompatibility = JavaVersion.VERSION_21
      targetCompatibility = JavaVersion.VERSION_21
    }
    project.extensions.findByType<AppExtension>()?.compileOptions {
      sourceCompatibility = JavaVersion.VERSION_21
      targetCompatibility = JavaVersion.VERSION_21
    }
    project.tasks.withType<JavaCompile>().configureEach {
      sourceCompatibility = JavaVersion.VERSION_21.toString()
      targetCompatibility = JavaVersion.VERSION_21.toString()
    }
  }
}
