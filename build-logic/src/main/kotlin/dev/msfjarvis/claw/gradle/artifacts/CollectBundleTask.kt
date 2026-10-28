/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle.artifacts

import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.io.path.createDirectories
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
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
    check(outputDir.toFile().deleteRecursively()) { "Cannot clean artifact directory: $outputDir" }
    outputDir.createDirectories()
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
