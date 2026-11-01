/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import java.io.ByteArrayOutputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "This is a one-shot network task that cannot be cached")
abstract class UploadZiplineReleaseTask : DefaultTask() {
  @get:Inject abstract val providers: ProviderFactory

  @get:InputDirectory
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val sourceDirectory: DirectoryProperty

  @get:Input abstract val ziplineApiVersion: Property<String>

  @TaskAction
  fun uploadRelease() {
    val envVar: (String) -> String? = { name ->
      providers.environmentVariable(name).orNull
    }
    check(!envVar("ZIPLINE_SIGNING_KEY").isNullOrBlank()) {
      "ZIPLINE_SIGNING_KEY must be set to upload a signed Zipline release"
    }
    val password = envVar("ADMIN_PASSWORD")
    check(!password.isNullOrBlank()) { "ADMIN_PASSWORD must be set to upload a Zipline release" }

    val source = sourceDirectory.get().asFile
    val files = source.walkTopDown().filter(File::isFile).toList()
    val manifest = files.singleOrNull { it.name == "manifest.zipline.json" }
    check(manifest != null) { "Missing manifest.zipline.json in $source" }
    check(files.any { it.extension == "zipline" }) {
      "No .zipline modules found in $source"
    }
    val duplicateNames = files.groupBy(File::getName).filterValues { it.size > 1 }.keys
    check(duplicateNames.isEmpty()) {
      "Cannot create flat Zipline archive; duplicate filenames: ${duplicateNames.joinToString()}"
    }

    val archive = ByteArrayOutputStream()
    ZipOutputStream(archive.buffered()).use { zip ->
      files.sortedBy(File::getName).forEach { file ->
        val bytes = file.readBytes()
        val crc = CRC32().apply { update(bytes) }
        val entry =
          ZipEntry(file.name).apply {
            method = ZipEntry.STORED
            size = bytes.size.toLong()
            compressedSize = size
            this.crc = crc.value
          }
        zip.putNextEntry(entry)
        zip.write(bytes)
        zip.closeEntry()
      }
    }

    val connection =
      URI("https://claw.msfjarvis.dev/admin/releases").toURL().openConnection() as HttpURLConnection
    try {
      connection.requestMethod = "POST"
      connection.connectTimeout = 30_000
      connection.readTimeout = 30_000
      connection.doOutput = true
      connection.instanceFollowRedirects = false
      connection.setRequestProperty("Authorization", "Bearer $password")
      connection.setRequestProperty("Content-Type", "application/zip")
      connection.setRequestProperty("X-Zipline-Version", ziplineApiVersion.get())
      connection.outputStream.use { output ->
        archive.toByteArray().inputStream().buffered().use { it.copyTo(output) }
      }

      val status = connection.responseCode
      val response =
        (if (status in 200..299) connection.inputStream else connection.errorStream)
          ?.bufferedReader()
          ?.use { it.readText() }
          .orEmpty()
      check(status in 200..299) {
        "Zipline release upload failed with HTTP $status" +
          (response.takeIf(String::isNotBlank)?.let { ": $it" } ?: "")
      }
      logger.lifecycle("Zipline release uploaded successfully: $response")
    } finally {
      connection.disconnect()
    }
  }
}
