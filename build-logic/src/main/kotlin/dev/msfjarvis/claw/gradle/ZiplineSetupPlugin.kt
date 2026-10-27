/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("UnstableApiUsage")

package dev.msfjarvis.claw.gradle

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.BuildConfigField
import groovy.json.JsonOutput
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

abstract class ZiplineSetupExtension {
  abstract val ziplineApiVersion: Property<String>
  abstract val devBuildTypes: ListProperty<String>
  abstract val enforceSignatureCheckBuildTypes: ListProperty<String>
}

internal fun ziplineVariantSettings(
  buildType: String?,
  version: String,
  devBuildTypes: List<String>,
  signatureBuildTypes: List<String>,
  devUrl: String?,
): Pair<String, Boolean> {
  val url =
    if (buildType in devBuildTypes) {
      devUrl ?: "http://10.0.2.2:8080/manifest.zipline.json"
    } else {
      "https://claw.msfjarvis.dev/current/$version/manifest.zipline.json"
    }
  return url to (buildType in signatureBuildTypes)
}

@Suppress("unused") // Reflectively invoked by Gradle
class ZiplineSetupPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val extension = project.extensions.create<ZiplineSetupExtension>("zipline")
    extension.devBuildTypes.convention(emptyList())
    extension.enforceSignatureCheckBuildTypes.convention(emptyList())

    val prepare =
      project.tasks.register<PrepareEmbeddedZiplineParserTask>("prepareEmbeddedZiplineParser") {
        sourceDirectory.set(
          project.isolated.rootProject.projectDirectory.dir(
            "zipline-parser/build/zipline/Production"
          )
        )
        outputDirectory.set(project.layout.buildDirectory.dir("generated/ziplineEmbeddedAssets"))
        dependsOn(":zipline-parser:compileProductionExecutableKotlinJsZipline")
      }

    project.pluginManager.withPlugin("com.android.application") {
      project.extensions.configure<ApplicationAndroidComponentsExtension> {
        onVariants { variant ->
          val version = extension.ziplineApiVersion.get()
          val (url, verify) =
            ziplineVariantSettings(
              variant.buildType,
              version,
              extension.devBuildTypes.get(),
              extension.enforceSignatureCheckBuildTypes.get(),
              project.providers.gradleProperty("claw.ziplineParserManifestUrl").orNull,
            )
          val fields =
            requireNotNull(variant.buildConfigFields) { "Zipline setup requires BuildConfig" }
          fields.put(
            "ZIPLINE_PARSER_MANIFEST_URL",
            BuildConfigField("String", JsonOutput.toJson(url), null),
          )
          fields.put(
            "ZIPLINE_PARSER_VERIFY_SIGNATURES",
            BuildConfigField("boolean", verify.toString(), null),
          )
          variant.sources.assets?.addGeneratedSourceDirectory(
            prepare,
            PrepareEmbeddedZiplineParserTask::outputDirectory,
          )
        }
      }
    }
  }
}
