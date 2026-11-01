/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import java.net.HttpURLConnection
import java.net.URI
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Downloads changing content from the network")
abstract class UpdateFixturesTask : DefaultTask() {
  @get:Input abstract val fixtures: MapProperty<String, String>

  @get:Internal abstract val outputDirectory: DirectoryProperty

  @TaskAction
  fun updateFixtures() {
    val mappings = fixtures.get()
    if (mappings.isEmpty()) return
    mappings.values.forEach(::validateFixtureFilename)
    check(mappings.values.toSet().size == mappings.size) {
      "Each fixture must have a unique output filename"
    }
    val downloads = mappings.map { (url, filename) ->
      val uri = URI(url)
      require(uri.scheme == "http" || uri.scheme == "https") {
        "Fixture URL must use HTTP or HTTPS: $url"
      }
      require(!uri.host.isNullOrBlank()) { "Fixture URL must have a host: $url" }
      uri to filename
    }

    val output = outputDirectory.get().asFile.toPath().toAbsolutePath().normalize()
    Files.createDirectories(output)
    val realOutput = output.toRealPath()

    downloads.forEach { (uri, filename) ->
      val url = uri.toString()
      val destination = realOutput.resolve(filename).normalize()
      check(destination.parent == realOutput) {
        "Fixture filename must be a single filename: $filename"
      }
      check(!Files.isSymbolicLink(destination)) {
        "Refusing to overwrite symbolic link fixture: $destination"
      }

      val connection = uri.toURL().openConnection() as HttpURLConnection
      val temporary = Files.createTempFile(realOutput, ".fixture-", ".tmp")
      try {
        connection.connectTimeout = 30_000
        connection.readTimeout = 30_000
        val status = connection.responseCode
        check(status in 200..299) { "Fixture download failed with HTTP $status for $url" }
        connection.inputStream.use { input ->
          Files.copy(input, temporary, StandardCopyOption.REPLACE_EXISTING)
        }
        try {
          Files.move(
            temporary,
            destination,
            StandardCopyOption.ATOMIC_MOVE,
            StandardCopyOption.REPLACE_EXISTING,
          )
        } catch (_: java.nio.file.AtomicMoveNotSupportedException) {
          Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING)
        }
      } finally {
        connection.disconnect()
        Files.deleteIfExists(temporary)
      }
    }
  }
}

internal fun validateFixtureFilename(filename: String) {
  require(filename.isNotBlank()) { "Fixture filename must not be blank" }
  require(filename != "." && filename != "..") { "Invalid fixture filename: $filename" }
  require('/' !in filename && '\\' !in filename) {
    "Fixture filename must be a single filename: $filename"
  }
  require(!java.nio.file.Path.of(filename).isAbsolute) {
    "Fixture filename must be relative: $filename"
  }
}
