/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import io.sentry.android.gradle.extensions.InstrumentationFeature
import io.sentry.android.gradle.extensions.SentryPluginExtension
import java.util.EnumSet
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

@Suppress("Unused", "UnstableApiUsage")
class SentryPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    val enableSentry = project.providers.gradleProperty(SENTRY_ENABLE_GRADLE_PROPERTY).isPresent
    val libs = project.extensions.getByName("libs") as LibrariesForLibs
    project.extensions.configure<ApplicationAndroidComponentsExtension> {
      onVariants(selector().all()) { variant ->
        val sentryDsn = project.providers.environmentVariable(SENTRY_DSN_PROPERTY).getOrElse("")
        variant.manifestPlaceholders.put("sentryDsn", sentryDsn)
        variant.manifestPlaceholders.put(
          "sentryEnvironment",
          if (variant.name.contains("release", true)) "production" else "dev",
        )
      }
    }
    if (!enableSentry) return
    project.plugins.apply(io.sentry.android.gradle.SentryPlugin::class)
    project.extensions.configure<SentryPluginExtension> {
      includeProguardMapping.set(true)
      autoUploadProguardMapping.set(true)
      uploadNativeSymbols.set(false)
      autoUploadNativeSymbols.set(true)
      includeNativeSources.set(false)
      ignoredVariants.set(emptySet())
      ignoredFlavors.set(emptySet())
      tracingInstrumentation {
        enabled.set(true)
        debug.set(false)
        logcat.enabled.set(true)
        forceInstrumentDependencies.set(false)
        features.set(EnumSet.allOf(InstrumentationFeature::class.java))
      }
      dexguardEnabled.set(false)
      autoInstallation {
        enabled.set(true)
        sentryVersion.set(libs.versions.sentry.sdk)
      }
      includeDependenciesReport.set(true)
      includeSourceContext.set(true)
      autoUploadSourceContext.set(true)
      additionalSourceDirsForSourceContext.set(emptySet())
      debug.set(false)
      org.set("claw")
      projectName.set("compose-lobsters")
      authToken.set(project.providers.environmentVariable("SENTRY_AUTH_TOKEN"))
      url.set(null as String?)
      telemetry.set(false)
      telemetryDsn.set(null as String?)
      sizeAnalysis {
        enabled.set(project.providers.environmentVariable("GITHUB_ACTIONS").isPresent)
      }
      vcsInfo {
        headSha.set(gitRevParse(project, "HEAD"))
        baseSha.set(
          exec(project, "git", "cat-file", "-p", "HEAD")
            .map { output -> output.lines().count { it.startsWith("parent") } }
            .flatMap { parentCount ->
              if (parentCount >= 2) {
                // Merge commit: use first parent
                gitRevParse(project, "HEAD^1")
              } else {
                // Normal commit: use last release
                exec(
                    project,
                    "gh",
                    "release",
                    "list",
                    "--limit",
                    "1",
                    "--json",
                    "tagName",
                    "-q",
                    ".[0].tagName",
                  )
                  .flatMap { tagName -> gitRevParse(project, tagName) }
              }
            }
        )
        vcsProvider.set("github")
        headRepoName.set("msfjarvis/compose-lobsters")
        baseRepoName.set("msfjarvis/compose-lobsters")
      }
    }
  }

  private companion object {

    private const val SENTRY_DSN_PROPERTY = "SENTRY_DSN"
    private const val SENTRY_ENABLE_GRADLE_PROPERTY = "enableSentry"

    private fun gitRevParse(project: Project, ref: String) = exec(project, "git", "rev-parse", ref)

    private fun exec(project: Project, vararg args: String) =
      project.providers
        .exec {
          commandLine(args)
          isIgnoreExitValue = true
        }
        .standardOutput
        .asText
        .map { it.trim() }
  }
}
