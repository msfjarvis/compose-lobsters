/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import com.github.benmanes.gradle.versions.VersionsPlugin
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import nl.littlerobots.vcu.plugin.VersionCatalogUpdateExtension
import nl.littlerobots.vcu.plugin.VersionCatalogUpdatePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType

@Suppress("Unused")
class DependencyUpdatesPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.pluginManager.apply(VersionsPlugin::class)
    project.pluginManager.apply(VersionCatalogUpdatePlugin::class)
    project.tasks.withType<DependencyUpdatesTask>().configureEach {
      rejectVersionIf {
        when (candidate.group) {
          "com.squareup.okhttp3",
          "org.jetbrains.kotlin" -> true
          else -> isNonStable(candidate.version) && !isNonStable(currentVersion)
        }
      }
      checkConstraints = true
      checkBuildEnvironmentConstraints = true
      checkForGradleUpdate = true
    }
    project.extensions.configure<VersionCatalogUpdateExtension> {
      keep.keepUnusedLibraries.set(true)
    }
  }

  private fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
  }
}
