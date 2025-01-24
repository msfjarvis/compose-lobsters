/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("UnstableApiUsage")

package dev.msfjarvis.claw.gradle

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.HasUnitTestBuilder
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.BaseExtension
import dev.msfjarvis.claw.gradle.LintConfig.configureLint
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.android.AndroidCacheFixPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.findByType

class AndroidCommonPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    project.configureSlimTests()
    project.pluginManager.apply(AndroidCacheFixPlugin::class)
    project.extensions.configure<BaseExtension> {
      compileSdkVersion(35)
      defaultConfig {
        minSdk = 26
        targetSdk = 35
      }

      packagingOptions {
        resources.excludes.add("**/*.version")
        resources.excludes.add("**/*.txt")
        resources.excludes.add("**/*.kotlin_module")
        resources.excludes.add("**/plugin.properties")
        resources.excludes.add("**/META-INF/AL2.0")
        resources.excludes.add("**/META-INF/LGPL2.1")
      }

      testOptions {
        animationsDisabled = true
        unitTests.isReturnDefaultValues = true
      }
    }
    project.extensions.findByType<ApplicationExtension>()?.lint?.configureLint(project)
    project.extensions.findByType<LibraryExtension>()?.lint?.configureLint(project)
    val libs = project.extensions.getByName("libs") as LibrariesForLibs
    project.dependencies.addProvider("lintChecks", libs.android.security.lints)
    project.dependencies.addProvider("lintChecks", libs.slack.compose.lints)
    project.dependencies.addProvider("lintChecks", libs.slack.lints)
  }

  private fun Project.configureSlimTests() {
    if (!providers.gradleProperty("slimTests").isPresent) {
      return
    }
    // Disable unit test tasks on the release build type for Android Library projects
    extensions.findByType<LibraryAndroidComponentsExtension>()?.run {
      beforeVariants(selector().all()) {
        if (it.name == "debug") return@beforeVariants
        (it as HasUnitTestBuilder).enableUnitTest = false
        it.androidTest.enable = false
      }
    }

    // Disable unit test tasks on the release build type for Android Application projects.
    extensions.findByType<ApplicationAndroidComponentsExtension>()?.run {
      beforeVariants(selector().all()) {
        if (it.name == "debug") return@beforeVariants
        (it as HasUnitTestBuilder).enableUnitTest = false
        it.androidTest.enable = false
      }
    }
  }
}
