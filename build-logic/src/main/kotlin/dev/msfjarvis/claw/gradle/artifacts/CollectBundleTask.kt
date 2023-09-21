/*
 * Copyright © 2022-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle.artifacts

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.createDirectory
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

abstract class CollectBundleTask : DefaultTask() {
  @get:InputFile
  @get:PathSensitive(PathSensitivity.NONE)
  abstract val bundleFile: RegularFileProperty

  @get:InputFile
  @get:PathSensitive(PathSensitivity.NONE)
  @get:Optional
  abstract val mappingFile: RegularFileProperty

  @get:Input abstract val variantName: Property<String>

  @get:Input abstract val versionName: Property<String>

  @get:OutputDirectory abstract val outputDirectory: DirectoryProperty

  override fun getGroup(): String {
    return "artifact collection"
  }

  @TaskAction
  fun taskAction() {
    val outputDir = outputDirectory.asFile.get().toPath()
    Files.walk(outputDir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete)
    outputDir.createDirectory()
    Files.copy(
      bundleFile.get().asFile.toPath(),
      outputDir.resolve("Claw-${variantName.get()}-${versionName.get()}.aab"),
      StandardCopyOption.REPLACE_EXISTING,
    )
    mappingFile.orNull?.apply {
      Files.copy(
        asFile.toPath(),
        outputDir.resolve("mapping.txt"),
        StandardCopyOption.REPLACE_EXISTING,
      )
    }
  }
}
