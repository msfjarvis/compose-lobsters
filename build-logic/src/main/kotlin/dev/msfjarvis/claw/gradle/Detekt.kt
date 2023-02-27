/*
 * Copyright © 2022-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

object Detekt {
  private const val COMPOSE_RULES_VERSION = "0.1.2"

  fun apply(project: Project) {
    project.pluginManager.apply(DetektPlugin::class.java)
    project.extensions.configure<DetektExtension> {
      debug = project.providers.gradleProperty("debugDetekt").isPresent
      parallel = true
      ignoredBuildTypes = listOf("benchmark", "release")
      basePath = project.layout.projectDirectory.toString()
      baseline =
        project.rootProject.layout.projectDirectory
          .dir("detekt-baselines")
          .file("${project.name}.xml")
          .asFile
    }
    project.dependencies.add(
      "detektPlugins",
      "io.nlopez.compose.rules:detekt:$COMPOSE_RULES_VERSION",
    )
  }
}
