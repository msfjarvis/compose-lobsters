/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import com.diffplug.spotless.LineEnding
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

@Suppress("Unused", "UnstableApiUsage")
class SpotlessPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    val isolated = project.isolated
    val isRoot = isolated.rootProject.projectDirectory == isolated.projectDirectory
    val rootFile: (String) -> RegularFile = { path ->
      isolated.rootProject.projectDirectory.file(path)
    }
    project.pluginManager.apply(SpotlessPlugin::class)
    // Invoking setLineEndings apparently fixes this bug that is causing CC invalidation
    // https://github.com/diffplug/spotless/issues/2416
    project.extensions.configure<SpotlessExtension> {
      kotlin {
        ktfmt(KTFMT_VERSION).googleStyle()
        target(if (isRoot) "build-logic/src/**/*.kt" else "src/**/*.kt")
        targetExclude("**/SentryNavigation3Integration.kt")
        licenseHeaderFile(rootFile("spotless/license.kt"))
        setLineEndings(LineEnding.UNIX)
      }
      kotlinGradle {
        ktfmt(KTFMT_VERSION).googleStyle()
        target(if (isRoot) listOf("*.kts", "build-logic/*.kts") else listOf("*.kts"))
        licenseHeaderFile(rootFile("spotless/license.kt"), "import|plugins|@file")
        setLineEndings(LineEnding.UNIX)
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
        setLineEndings(LineEnding.UNIX)
      }
    }
  }

  private companion object {
    private const val KTFMT_VERSION = "0.64"
  }
}
