/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.aps.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

@Suppress("Unused")
class KotlinLibraryPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    project.pluginManager.apply(KotlinCommonPlugin::class)
  }
}
