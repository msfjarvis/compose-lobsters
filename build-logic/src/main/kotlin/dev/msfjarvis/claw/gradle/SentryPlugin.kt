/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import io.sentry.android.gradle.extensions.InstrumentationFeature
import io.sentry.android.gradle.extensions.SentryPluginExtension
import java.util.EnumSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

@Suppress("Unused")
class SentryPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    if (project.providers.environmentVariable("CI_BENCHMARK").isPresent) return
    project.pluginManager.withPlugin("com.android.application") {
      val catalog = project.extensions.getByType<VersionCatalogsExtension>()
      val libs = catalog.named("libs")
      project.extensions.configure<ApplicationAndroidComponentsExtension> {
        onVariants(selector().all()) { variant ->
          val sentryDsn = project.providers.environmentVariable(SENTRY_DSN_PROPERTY)
          variant.manifestPlaceholders.put("sentryDsn", sentryDsn.getOrElse(""))
        }
      }
      project.plugins.apply(io.sentry.android.gradle.SentryPlugin::class)
      project.extensions.configure<SentryPluginExtension> {
        val enableMappings =
          project.providers.gradleProperty(SENTRY_UPLOAD_MAPPINGS_PROPERTY).isPresent
        includeProguardMapping.set(enableMappings)
        autoUploadProguardMapping.set(enableMappings)
        uploadNativeSymbols.set(false)
        autoUploadNativeSymbols.set(false)
        includeNativeSources.set(false)
        ignoredVariants.set(emptySet())
        ignoredBuildTypes.set(setOf("benchmark", "debug"))
        ignoredFlavors.set(emptySet())
        tracingInstrumentation {
          enabled.set(true)
          debug.set(false)
          forceInstrumentDependencies.set(false)
          features.set(EnumSet.allOf(InstrumentationFeature::class.java))
        }
        experimentalGuardsquareSupport.set(false)
        autoInstallation {
          enabled.set(true)
          sentryVersion.set(libs.findVersion("sentry-sdk").get().requiredVersion)
        }
        includeDependenciesReport.set(true)
      }
      with(project.dependencies) {
        addProvider("implementation", platform(libs.findLibrary("sentry-bom").get()))
      }
    }
  }

  private companion object {

    private const val SENTRY_DSN_PROPERTY = "SENTRY_DSN"
    private const val SENTRY_UPLOAD_MAPPINGS_PROPERTY = "sentryUploadMappings"
  }
}
