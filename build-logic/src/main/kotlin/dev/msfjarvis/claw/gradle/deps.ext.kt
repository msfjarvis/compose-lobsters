/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.exclude

/** Extension function to configure JUnit5 dependencies with the Truth assertion library. */
fun DependencyHandlerScope.addTestDependencies(project: Project) {
  val libs = project.extensions.getByName("libs") as LibrariesForLibs
  addProvider("testImplementation", libs.junit.jupiter.api)
  addProvider<MinimalExternalModuleDependency, ExternalModuleDependency>(
    "testImplementation",
    libs.truth,
  ) {
    exclude(group = "junit", module = "junit")
  }
  addProvider("testRuntimeOnly", libs.junit.jupiter.engine)
  addProvider<MinimalExternalModuleDependency, ExternalModuleDependency>(
    "testRuntimeOnly",
    libs.junit.legacy,
  ) {
    // See https://github.com/google/truth/issues/333
    because("Truth needs it")
  }
}
