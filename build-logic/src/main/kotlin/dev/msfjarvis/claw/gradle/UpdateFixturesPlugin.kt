/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.MapProperty
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

abstract class UpdateFixturesExtension {
  abstract val fixtures: MapProperty<String, String>
}

@Suppress("unused") // Reflectively invoked by Gradle
class UpdateFixturesPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val extension = project.extensions.create<UpdateFixturesExtension>("fixtures")
    extension.fixtures.convention(emptyMap())

    val updateFixtures =
      project.tasks.register<UpdateFixturesTask>("updateFixtures") {
        group = "verification"
        description = "Download configured HTML test fixtures"
        outputs.upToDateWhen { false }
        fixtures.set(extension.fixtures)
        outputDirectory.set(project.layout.projectDirectory.dir("src/testFixtures/resources"))
      }

    // Keep explicitly combined invocations deterministic without making ordinary resource
    // processing trigger network downloads.
    project.tasks
      .matching { it.name == "processTestFixturesResources" }
      .configureEach { mustRunAfter(updateFixtures) }
  }
}
