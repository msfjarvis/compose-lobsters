/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.exclude
import org.gradle.kotlin.dsl.getByType

/** Extension function to configure JUnit5 dependencies with the Truth assertion library. */
fun DependencyHandlerScope.addTestDependencies(project: Project) {
  val catalog = project.extensions.getByType<VersionCatalogsExtension>()
  val libs = catalog.named("libs")
  addProvider("testImplementation", libs.findLibrary("junit-jupiter-api").get())
  addProvider<MinimalExternalModuleDependency, ExternalModuleDependency>(
    "testImplementation",
    libs.findLibrary("truth").get(),
  ) {
    exclude(group = "junit", module = "junit")
  }
  addProvider("testRuntimeOnly", libs.findLibrary("junit-jupiter-engine").get())
  addProvider<MinimalExternalModuleDependency, ExternalModuleDependency>(
    "testRuntimeOnly",
    libs.findLibrary("junit-legacy").get(),
  ) {
    // See https://github.com/google/truth/issues/333
    because("Truth needs it")
  }
}
