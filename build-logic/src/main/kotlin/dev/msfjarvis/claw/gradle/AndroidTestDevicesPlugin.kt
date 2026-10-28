/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

@Suppress("UnstableApiUsage")
class AndroidTestDevicesPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    listOf("com.android.application", "com.android.test").forEach { id ->
      project.pluginManager.withPlugin(id) {
        project.extensions.configure<CommonExtension> {
          testOptions.managedDevices.localDevices.create("pixel9api36") {
            device = "Pixel 9"
            apiLevel = 36
            systemImageSource = "aosp"
          }
        }
      }
    }
  }
}
