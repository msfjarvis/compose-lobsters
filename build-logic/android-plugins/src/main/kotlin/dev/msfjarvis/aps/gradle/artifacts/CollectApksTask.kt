/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.aps.gradle.artifacts

import com.android.build.api.variant.BuiltArtifactsLoader
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
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
  abstract val mappingFile: RegularFileProperty

  @get:Input abstract val variantName: Property<String>

  @get:Internal abstract val builtArtifactsLoader: Property<BuiltArtifactsLoader>

  @get:OutputDirectory abstract val outputDirectory: DirectoryProperty

  @TaskAction
  fun run() {
    val outputDir = outputDirectory.asFile.get()
    val outputDirStream =
      Files.walk(outputDir.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile)
    outputDirStream.forEach(File::delete)
    outputDirStream.close()
    outputDir.mkdirs()
    val builtArtifacts =
      builtArtifactsLoader.get().load(apkFolder.get()) ?: error("Cannot load APKs")
    builtArtifacts.elements.forEach { artifact ->
      Files.copy(
        Paths.get(artifact.outputFile),
        outputDir.resolve("Claw-${variantName.get()}-${artifact.versionName}.apk").toPath(),
        StandardCopyOption.REPLACE_EXISTING,
      )
    }
    Files.copy(
      mappingFile.get().asFile.toPath(),
      outputDir.resolve("mapping.txt").toPath(),
      StandardCopyOption.REPLACE_EXISTING,
    )
  }
}
