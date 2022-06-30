package dev.msfjarvis.aps.gradle

import com.github.benmanes.gradle.versions.VersionsPlugin
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import java.util.Locale
import nl.littlerobots.vcu.plugin.VersionCatalogUpdateExtension
import nl.littlerobots.vcu.plugin.VersionCatalogUpdatePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

@Suppress("Unused")
class DependencyUpdatesPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.pluginManager.apply(VersionsPlugin::class)
    project.pluginManager.apply(VersionCatalogUpdatePlugin::class)
    project.tasks.withType<DependencyUpdatesTask>().configureEach {
      rejectVersionIf {
        when (candidate.group) {
          "com.android.application",
          "com.android.library",
          "com.google.accompanist",
          "org.jetbrains.kotlin" -> true
          else -> isNonStable(candidate.version) && !isNonStable(currentVersion)
        }
      }
      checkForGradleUpdate = false
    }
    project.extensions.getByType<VersionCatalogUpdateExtension>().run {
      keep.keepUnusedLibraries.set(true)
    }
  }

  private fun isNonStable(version: String): Boolean {
    val stableKeyword =
      listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase(Locale.ROOT).contains(it) }
    val regex = "^[\\d,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
  }
}
