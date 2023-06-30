/*
 * Copyright © 2022-2023 Harsh Shandilya.
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
import org.gradle.android.AndroidCacheFixPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType

class AndroidCommonPlugin : Plugin<Project> {

  private companion object {
    const val COMPILE_SDK = 34
    const val MIN_SDK = 26
    const val TARGET_SDK = 34
    const val SLIM_TESTS_PROPERTY = "slimTests"
  }

  override fun apply(project: Project) {
    project.configureSlimTests()
    project.pluginManager.apply(AndroidCacheFixPlugin::class)
    project.extensions.configure<BaseExtension> {
      compileSdkVersion(COMPILE_SDK)
      defaultConfig {
        minSdk = MIN_SDK
        targetSdk = TARGET_SDK
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
    val catalog = project.extensions.getByType<VersionCatalogsExtension>()
    val libs = catalog.named("libs")
    project.dependencies.addProvider("lintChecks", libs.findLibrary("slack-compose-lints").get())
  }

  private fun Project.configureSlimTests() {
    if (!providers.gradleProperty(SLIM_TESTS_PROPERTY).isPresent) {
      return
    }
    // Disable unit test tasks on the release build type for Android Library projects
    extensions.findByType<LibraryAndroidComponentsExtension>()?.run {
      beforeVariants(selector().withBuildType("release")) {
        (it as HasUnitTestBuilder).enableUnitTest = false
        it.enableAndroidTest = false
      }
    }

    // Disable unit test tasks on the release build type for Android Application projects.
    extensions.findByType<ApplicationAndroidComponentsExtension>()?.run {
      beforeVariants(selector().withBuildType("release")) {
        (it as HasUnitTestBuilder).enableUnitTest = false
        it.enableAndroidTest = false
      }
    }
  }
}
