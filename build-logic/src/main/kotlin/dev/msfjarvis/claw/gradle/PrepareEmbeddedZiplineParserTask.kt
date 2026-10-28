/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class PrepareEmbeddedZiplineParserTask : DefaultTask() {
  @get:InputDirectory
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val sourceDirectory: DirectoryProperty

  @get:OutputDirectory abstract val outputDirectory: DirectoryProperty

  @TaskAction
  fun prepare() {
    val sourceDir = sourceDirectory.get().asFile
    val ziplineDir = File(outputDirectory.get().asFile, "zipline")
    ziplineDir.deleteRecursively()
    ziplineDir.mkdirs()
    val manifest = File(sourceDir, "manifest.zipline.json")
    check(manifest.exists()) { "Missing Zipline manifest: $manifest" }
    @Suppress("UNCHECKED_CAST")
    val parsed = JsonSlurper().parse(manifest) as MutableMap<String, Any?>
    val modules = parsed["modules"] as? Map<*, *>
    check(!modules.isNullOrEmpty()) { "Zipline manifest contains no modules: $manifest" }
    for ((_, moduleValue) in modules) {
      val module = moduleValue as Map<*, *>
      val sourceFile = File(sourceDir, module["url"] as String)
      check(sourceFile.exists()) { "Missing Zipline module: $sourceFile" }
      sourceFile.copyTo(File(ziplineDir, module["sha256"] as String), overwrite = true)
    }
    val unsigned =
      ((parsed["unsigned"] as? Map<*, *>)?.toMutableMap() ?: mutableMapOf()).apply {
        // Embedded freshness is decided by the application, not by the build clock.
        put("freshAtEpochMs", 0L)
      }
    parsed["unsigned"] = unsigned
    File(ziplineDir, "zipline-parser.manifest.zipline.json").writeText(JsonOutput.toJson(parsed))
  }
}
