/*
 * Copyright © Harsh Shandilya.
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
  arrayOf("test", "androidTest", "screenshotTest")
    .filter { sourceSet -> project.configurations.findByName("${sourceSet}Implementation") != null }
    .forEach { sourceSet ->
      addProvider("${sourceSet}Implementation", libs.junit.jupiter.api)
      addProvider<MinimalExternalModuleDependency, ExternalModuleDependency>(
        "${sourceSet}Implementation",
        libs.truth,
      ) {
        exclude(group = "junit", module = "junit")
      }
      addProvider("${sourceSet}RuntimeOnly", libs.junit.jupiter.engine)
      addProvider<MinimalExternalModuleDependency, ExternalModuleDependency>(
        "${sourceSet}RuntimeOnly",
        libs.junit.legacy,
      ) {
        // See https://github.com/google/truth/issues/333
        because("Truth needs it")
      }
    }
}
