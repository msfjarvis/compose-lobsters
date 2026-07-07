/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

@Suppress("Unused", "UnstableApiUsage")
class SpotlessPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    if (project.isolated.rootProject == project.isolated) {
      throw GradleException("Spotless plugin must only be applied to the root project.")
    }
    val isolated = project.isolated
    val rootFile: (String) -> RegularFile = { path ->
      isolated.rootProject.projectDirectory.file(path)
    }
    project.pluginManager.apply(SpotlessPlugin::class)
    project.extensions.configure<SpotlessExtension> {
      kotlin {
        ktfmt(KTFMT_VERSION).googleStyle()
        target("src/**/*.kt")
        targetExclude("**/SentryNavigation3Integration.kt")
        licenseHeaderFile(rootFile("spotless/license.kt"))
      }
      kotlinGradle {
        ktfmt(KTFMT_VERSION).googleStyle()
        target("*.kts")
        licenseHeaderFile(rootFile("spotless/license.kt"), "import|plugins|@file")
      }
      format("xml") {
        target("src/**/*.xml")
        trimTrailingWhitespace()
        leadingTabsToSpaces()
        endWithNewline()
        licenseHeaderFile(
          rootFile("spotless/license.xml"),
          "<(adaptive-icon|appwidget-provider|data-extraction-rules|full-backup-content|manifest|network-security-config|vector|resources)",
        )
      }
    }
  }

  private companion object {
    private const val KTFMT_VERSION = "0.64"
  }
}
