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
import kotlin.test.assertTrue
import org.gradle.testkit.runner.GradleRunner

class UpdateFixturesPluginFunctionalTest {
  @Test
  fun downloadsConfiguredFixturesOnEachCombinedInvocation() {
    val response = AtomicReference("first version")
    val requests = AtomicInteger()
    withFixtureServer(response, requests) { url ->
      withProject("fixtures { fixtures.put(\"$url\", \"downloaded.html\") }") { project ->
        val firstRun = run(project, "updateFixtures", "processTestFixturesResources")
        val downloaded = project.resolve("src/testFixtures/resources/downloaded.html")
        val processed = project.resolve("build/resources/testFixtures/downloaded.html")
        assertEquals("first version", Files.readString(downloaded))
        assertEquals("first version", Files.readString(processed))
        assertEquals(1, requests.get())
        assertTrue(
          firstRun.output.indexOf(":updateFixtures") <
            firstRun.output.indexOf(":processTestFixturesResources")
        )

        // Resource processing by itself must not trigger a download, even when the plugin is
        // configured. Force the resource task to execute by removing its previous output.
        Files.delete(processed)
        response.set("second version")
        run(project, "processTestFixturesResources")
        assertEquals("first version", Files.readString(downloaded))
        assertEquals(1, requests.get())

        run(project, "updateFixtures", "processTestFixturesResources")
        assertEquals("second version", Files.readString(downloaded))
        assertEquals("second version", Files.readString(processed))
        assertEquals(2, requests.get())
      }
    }
  }

  @Test
  fun emptyFixtureMapDoesNotChangeExistingResourcesOrDownload() {
    val response = AtomicReference("unexpected")
    val requests = AtomicInteger()
    withFixtureServer(response, requests) {
      withProject("") { project ->
        val existingResource = project.resolve("src/testFixtures/resources/keep.html")
        Files.createDirectories(existingResource.parent)
        Files.writeString(existingResource, "keep me")

        run(project, "updateFixtures", "processTestFixturesResources")

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
      .withPluginClasspath()
      .withArguments(*tasks, "--stacktrace")
      .build()

  private fun withProject(fixturesConfiguration: String, block: (Path) -> Unit) {
    val project = Files.createTempDirectory("update-fixtures-test")
    try {
      Files.writeString(
        project.resolve("settings.gradle.kts"),
        "rootProject.name = \"fixture-test\"",
      )
      Files.writeString(
        project.resolve("build.gradle.kts"),
        """
        plugins {
          id("dev.msfjarvis.claw.update-fixtures")
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
