/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.VariantOutputConfiguration
import dev.msfjarvis.claw.gradle.artifacts.CollectApksTask
import dev.msfjarvis.claw.gradle.artifacts.CollectBundleTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register

@Suppress("Unused")
class RenameArtifactsPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    project.pluginManager.withPlugin("com.android.application") {
      project.extensions.configure<ApplicationAndroidComponentsExtension> {
        onVariants { variant ->
          val taskPrefix = "collect${variant.name.replaceFirstChar { it.uppercase() }}"
          project.tasks.register<CollectApksTask>("${taskPrefix}Apks") {
            variantName.set(variant.name)
            apkFolder.set(variant.artifacts.get(SingleArtifact.APK))
            mappingFile.set(variant.artifacts.get(SingleArtifact.OBFUSCATION_MAPPING_FILE))
            builtArtifactsLoader.set(variant.artifacts.getBuiltArtifactsLoader())
            outputDirectory.set(project.layout.projectDirectory.dir("apk"))
          }
          project.tasks.register<CollectBundleTask>("${taskPrefix}Bundle") {
            val mainOutput =
              variant.outputs.single {
                it.outputType == VariantOutputConfiguration.OutputType.SINGLE
              }
            variantName.set(variant.name)
            versionName.set(mainOutput.versionName)
            mappingFile.set(variant.artifacts.get(SingleArtifact.OBFUSCATION_MAPPING_FILE))
            bundleFile.set(variant.artifacts.get(SingleArtifact.BUNDLE))
            outputDirectory.set(project.layout.projectDirectory.dir("bundle"))
          }
        }
      }
    }
  }
}
