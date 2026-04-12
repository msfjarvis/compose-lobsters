/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("UnstableApiUsage")

package dev.msfjarvis.claw.gradle

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.AppPlugin
import dev.msfjarvis.claw.gradle.signing.configureBuildSigning
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

@Suppress("Unused")
class ApplicationPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    project.pluginManager.apply(AppPlugin::class)
    project.pluginManager.apply(AndroidCommonPlugin::class)
    project.extensions.configure<ApplicationExtension> {
      defaultConfig { targetSdk = TARGET_SDK }
      dependenciesInfo {
        includeInBundle = false
        includeInApk = false
      }

      buildFeatures {
        viewBinding = true
        buildConfig = true
      }

      buildTypes.configureEach {
        setProguardFiles(
          listOf("r8-android-optimize.pro", "r8-rules.pro", "r8-rules-missing-classes.pro")
        )
        when (name) {
          "release" -> {
            isMinifyEnabled = !project.providers.environmentVariable("DISABLE_MINIFY").isPresent
          }
          "debug" -> {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isMinifyEnabled = false
          }
        }
      }
      project.configureBuildSigning()
    }
  }
}
