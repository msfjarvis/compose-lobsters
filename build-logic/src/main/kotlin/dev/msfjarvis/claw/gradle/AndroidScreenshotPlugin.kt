/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import com.android.build.api.dsl.LibraryExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

@Suppress("UnstableApiUsage")
class AndroidScreenshotPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.pluginManager.apply("com.android.compose.screenshot")
    project.pluginManager.withPlugin("com.android.library") {
      project.extensions.configure<LibraryExtension> {
        androidResources.enable = true
        buildFeatures.compose = true
        experimentalProperties["android.experimental.enableScreenshotTest"] = true
        // JUnit's test artifacts each bundle the same license resources.
        packaging.resources.merges += setOf("META-INF/LICENSE.md", "META-INF/LICENSE-notice.md")
        defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
      }
      val libs = project.extensions.getByName("libs") as LibrariesForLibs
      project.dependencies.apply {
        addProvider("screenshotTestImplementation", libs.screenshot.validation.api)
        addProvider("screenshotTestImplementation", libs.androidx.compose.ui.tooling.asProvider())
        addProvider("androidTestImplementation", libs.androidx.compose.ui.test.junit4)
        addProvider("androidTestImplementation", libs.androidx.test.runner)
        addProvider("androidTestImplementation", libs.androidx.test.ext.junit)
        addProvider("debugImplementation", libs.androidx.compose.ui.test.manifest)
      }
    }
  }
}
