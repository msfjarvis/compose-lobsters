/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

@Suppress("Unused")
class LibraryPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    project.pluginManager.apply(LibraryPlugin::class)
    project.pluginManager.apply(AndroidCommonPlugin::class)
  }
}
