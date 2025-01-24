/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle.artifacts

import com.android.build.api.variant.BuiltArtifactsLoader
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import kotlin.io.path.createDirectory
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

/** Task to collect APKs in a given [outputDirectory]. */
@CacheableTask
abstract class CollectApksTask : DefaultTask() {
  @get:InputFiles @get:PathSensitive(PathSensitivity.NONE) abstract val apkFolder: DirectoryProperty

  @get:InputFile
  @get:PathSensitive(PathSensitivity.NONE)
  @get:Optional
  abstract val mappingFile: RegularFileProperty

  @get:Input abstract val variantName: Property<String>

  @get:Internal abstract val builtArtifactsLoader: Property<BuiltArtifactsLoader>

  @get:OutputDirectory abstract val outputDirectory: DirectoryProperty

  override fun getGroup(): String {
    return "artifact collection"
  }

  @TaskAction
  fun run() {
    val outputDir = outputDirectory.asFile.get().toPath()
    Files.walk(outputDir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete)
    outputDir.createDirectory()
    val builtArtifacts =
      builtArtifactsLoader.get().load(apkFolder.get()) ?: error("Cannot load APKs")
    builtArtifacts.elements.forEach { artifact ->
      Files.copy(
        Paths.get(artifact.outputFile),
        outputDir.resolve("Claw-${variantName.get()}-${artifact.versionName}.apk"),
        StandardCopyOption.REPLACE_EXISTING,
      )
    }
    mappingFile.orNull?.apply {
      Files.copy(
        asFile.toPath(),
        outputDir.resolve("mapping.txt"),
        StandardCopyOption.REPLACE_EXISTING,
      )
    }
  }
}
