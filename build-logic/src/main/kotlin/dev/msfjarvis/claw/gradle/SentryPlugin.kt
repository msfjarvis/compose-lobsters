/*
 * Copyright © 2023-2024 Harsh Shandilya.
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

@Suppress("Unused")
class SentryPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    project.pluginManager.withPlugin("com.android.application") {
      val libs = project.extensions.getByName("libs") as LibrariesForLibs
      project.extensions.configure<ApplicationAndroidComponentsExtension> {
        onVariants(selector().all()) { variant ->
          val sentryDsn = project.providers.environmentVariable(SENTRY_DSN_PROPERTY)
          val enableSentry =
            project.providers.gradleProperty(SENTRY_ENABLE_GRADLE_PROPERTY).isPresent
          variant.manifestPlaceholders.put("sentryDsn", sentryDsn.getOrElse(""))
          variant.manifestPlaceholders.put(
            "sentryEnvironment",
            when {
              variant.name.contains("release", true) && enableSentry -> "production"
              variant.name.contains("release", true) && !enableSentry -> "nightly"
              else -> "dev"
            },
          )
        }
      }
      project.plugins.apply(io.sentry.android.gradle.SentryPlugin::class)
      project.extensions.configure<SentryPluginExtension> {
        includeProguardMapping.set(true)
        autoUploadProguardMapping.set(true)
        uploadNativeSymbols.set(false)
        autoUploadNativeSymbols.set(false)
        includeNativeSources.set(false)
        ignoredVariants.set(emptySet())
        ignoredBuildTypes.set(setOf("benchmark"))
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
        url.set(null)
        telemetry.set(false)
        telemetryDsn.set(null)
      }
      with(project.dependencies) {
        addProvider("implementation", platform(libs.sentry.bom))
        addProvider("implementation", libs.sentry.android)
      }
    }
  }

  private companion object {

    private const val SENTRY_DSN_PROPERTY = "SENTRY_DSN"
    private const val SENTRY_ENABLE_GRADLE_PROPERTY = "enableSentry"
  }
}
