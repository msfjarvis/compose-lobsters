/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import org.gradle.testkit.runner.GradleRunner

class UpdateFixturesPluginFunctionalTest {
  @Test
  fun emptyFixtureMapDoesNotChangeExistingResourcesOrDownload() {
    val response = AtomicReference("unexpected")
    val requests = AtomicInteger()
    withFixtureServer(response, requests) {
      withProject("") { project ->
        val existingResource = project.resolve("src/testFixtures/resources/keep.html")
        Files.createDirectories(existingResource.parent)
        Files.writeString(existingResource, "keep me")

        run(project, "updateFixtures")

        assertEquals("keep me", Files.readString(existingResource))
        assertEquals(
          "keep me",
          Files.readString(project.resolve("build/resources/testFixtures/keep.html")),
        )
        assertEquals(0, requests.get())
        assertFalse(Files.exists(project.resolve("src/testFixtures/resources/unexpected.html")))
      }
    }
  }

  private fun run(project: Path, vararg tasks: String) =
    GradleRunner.create()
      .withProjectDir(project.toFile())
      .withArguments(*tasks, "--stacktrace")
      .build()

  private fun withProject(fixturesConfiguration: String, block: (Path) -> Unit) {
    val functionalTestRepo =
      requireNotNull(System.getProperty("com.autonomousapps.plugin-under-test.repo"))
    val pluginVersion =
      requireNotNull(System.getProperty("com.autonomousapps.plugin-under-test.version"))
    val project = Files.createTempDirectory("update-fixtures-test")
    try {
      Files.writeString(
        project.resolve("settings.gradle.kts"),
        """
        pluginManagement {
          repositories {
            maven { url = uri("$functionalTestRepo") }
            google()
            mavenCentral()
            gradlePluginPortal()
          }
        }

        rootProject.name = "fixture-test"
        """
          .trimIndent(),
      )
      Files.writeString(
        project.resolve("build.gradle.kts"),
        """
        plugins {
          id("dev.msfjarvis.claw.update-fixtures") version "$pluginVersion"
          `java-test-fixtures`
        }

        $fixturesConfiguration
        """
          .trimIndent(),
      )
      block(project)
    } finally {
      project.toFile().deleteRecursively()
    }
  }

  private fun withFixtureServer(
    response: AtomicReference<String>,
    requests: AtomicInteger,
    block: (String) -> Unit,
  ) {
    val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
    server.createContext("/fixture.html") { exchange ->
      requests.incrementAndGet()
      val body = response.get().toByteArray()
      exchange.sendResponseHeaders(200, body.size.toLong())
      exchange.responseBody.use { it.write(body) }
    }
    server.start()
    try {
      block("http://127.0.0.1:${server.address.port}/fixture.html")
    } finally {
      server.stop(0)
    }
  }
}
