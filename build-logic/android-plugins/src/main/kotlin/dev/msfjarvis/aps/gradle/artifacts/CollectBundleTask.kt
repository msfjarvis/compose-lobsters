/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.aps.gradle.artifacts

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class CollectBundleTask : DefaultTask() {
  @get:InputFile abstract val bundleFile: RegularFileProperty

  @get:InputFile abstract val mappingFile: RegularFileProperty

  @get:Input abstract val variantName: Property<String>

  @get:Input abstract val versionName: Property<String>

  @get:OutputDirectory abstract val outputDirectory: DirectoryProperty

  @TaskAction
  fun taskAction() {
    val outputDir = outputDirectory.asFile.get()
    val outputDirStream =
      Files.walk(outputDir.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile)
    outputDirStream.forEach(File::delete)
    outputDirStream.close()
    outputDir.mkdirs()
    Files.copy(
      bundleFile.get().asFile.toPath(),
      outputDir.resolve("Claw-${variantName.get()}-${versionName.get()}.aab").toPath(),
      StandardCopyOption.REPLACE_EXISTING,
    )
    Files.copy(
      mappingFile.get().asFile.toPath(),
      outputDir.resolve("mapping.txt").toPath(),
      StandardCopyOption.REPLACE_EXISTING,
    )
  }
}
