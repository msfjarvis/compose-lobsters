/*
 * Copyright © 2022-2023 Harsh Shandilya.
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
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

@Suppress("Unused")
class SpotlessPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    if (project.rootProject != project) {
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
        indentWithSpaces()
        endWithNewline()
        licenseHeaderFile(
          project.file("spotless/license.xml"),
          "<manifest|<resources|<full-backup-content|<data-extraction-rules|<adaptive-icon"
        )
      }
    }
  }

  private companion object {
    private const val KTFMT_VERSION = "0.44"
  }
}
