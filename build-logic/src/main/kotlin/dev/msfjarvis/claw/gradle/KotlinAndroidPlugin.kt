/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.TestExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

@Suppress("Unused")
class KotlinAndroidPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    project.pluginManager.run { apply(KotlinCommonPlugin::class) }
    project.pluginManager.withPlugin("com.android.library") {
      project.extensions.getByType<LibraryExtension>().compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
      }
    }
    project.pluginManager.withPlugin("com.android.application") {
      project.extensions.getByType<ApplicationExtension>().compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
      }
    }
    project.pluginManager.withPlugin("com.android.test") {
      project.extensions.getByType<TestExtension>().compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
      }
    }
    project.tasks.withType<JavaCompile>().configureEach {
      sourceCompatibility = JavaVersion.VERSION_21.toString()
      targetCompatibility = JavaVersion.VERSION_21.toString()
    }
  }
}
