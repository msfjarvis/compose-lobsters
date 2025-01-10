/*
 * Copyright © 2022-2025 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import com.diffplug.gradle.spotless.SpotlessTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType

@Suppress("Unused", "UnstableApiUsage")
class SpotlessPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    if (project.isolated.rootProject != project.isolated) {
      throw GradleException("Spotless plugin must only be applied to the root project.")
    }
    project.pluginManager.apply(SpotlessPlugin::class)
    project.extensions.configure<SpotlessExtension> {
      kotlin {
        ktfmt(KTFMT_VERSION).googleStyle()
        target("**/*.kt")
        targetExclude("**/build/", "/spotless/")
        licenseHeaderFile(project.file("spotless/license.kt"))
      }
      kotlinGradle {
        ktfmt(KTFMT_VERSION).googleStyle()
        target("**/*.kts")
        targetExclude("**/build/")
        licenseHeaderFile(project.file("spotless/license.kt"), "import|plugins|@file")
      }
      format("xml") {
        target("**/*.xml")
        targetExclude("**/build/", ".idea/", "/spotless/", "**/lint-baseline.xml")
        trimTrailingWhitespace()
        leadingTabsToSpaces()
        endWithNewline()
        licenseHeaderFile(
          project.file("spotless/license.xml"),
          "<(adaptive-icon|appwidget-provider|data-extraction-rules|full-backup-content|manifest|vector|resources)",
        )
      }
    }
    project.tasks.withType<SpotlessTask>().configureEach {
      notCompatibleWithConfigurationCache("https://github.com/diffplug/spotless/issues/987")
    }
  }

  private companion object {
    private const val KTFMT_VERSION = "0.53"
  }
}
