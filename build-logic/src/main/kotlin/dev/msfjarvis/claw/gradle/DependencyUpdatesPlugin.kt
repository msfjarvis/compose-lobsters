/*
 * Copyright © 2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import com.github.zafarkhaja.semver.Version
import kotlin.jvm.optionals.getOrNull
import nl.littlerobots.vcu.plugin.VersionCatalogUpdateExtension
import nl.littlerobots.vcu.plugin.VersionCatalogUpdatePlugin
import nl.littlerobots.vcu.plugin.versionSelector
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

@Suppress("Unused")
class DependencyUpdatesPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.pluginManager.apply(VersionCatalogUpdatePlugin::class)
    project.extensions.configure<VersionCatalogUpdateExtension> {
      keep.keepUnusedLibraries.set(true)
      versionSelector {
        val currentVersion = Version.tryParse(it.currentVersion).getOrNull()
        val newVersion = Version.tryParse(it.candidate.version).getOrNull()
        if (currentVersion == null || newVersion == null) {
          false
        } else {
          newVersion.isHigherThan(currentVersion)
        }
      }
    }
  }
}
