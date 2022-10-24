package dev.msfjarvis.aps.gradle

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.VariantOutputConfiguration
import dev.msfjarvis.aps.gradle.artifacts.CollectApksTask
import dev.msfjarvis.aps.gradle.artifacts.CollectBundleTask
import java.util.Locale
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register

@Suppress("Unused")
class RenameArtifactsPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    project.pluginManager.withPlugin("com.android.application") {
      project.extensions.getByType<ApplicationAndroidComponentsExtension>().run {
        onVariants { variant ->
          val taskPrefix = "collect${variant.name.capitalize(Locale.ROOT)}"
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
