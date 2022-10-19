package dev.msfjarvis.aps.gradle

import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

object Detekt {
  private const val TWITTER_RULES_VERSION = "0.0.21"

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
      "com.twitter.compose.rules:detekt:$TWITTER_RULES_VERSION",
    )
  }
}
