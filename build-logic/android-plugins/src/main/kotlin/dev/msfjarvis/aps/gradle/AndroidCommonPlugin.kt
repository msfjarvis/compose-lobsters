/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("UnstableApiUsage")

package dev.msfjarvis.aps.gradle

import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.Lint
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.android.AndroidCacheFixPlugin
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.findByType

private const val SLIM_TESTS_PROPERTY = "slimTests"

class AndroidCommonPlugin : Plugin<Project> {

  private companion object {
    const val COMPILE_SDK = 33
    const val MIN_SDK = 26
    const val TARGET_SDK = 33
  }

  override fun apply(project: Project) {
    project.configureSlimTests()
    project.pluginManager.apply(AndroidCacheFixPlugin::class)
    project.extensions.findByType<BaseExtension>()?.run {
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

      compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
      }

      testOptions {
        animationsDisabled = true
        unitTests.isReturnDefaultValues = true
      }
    }
    project.extensions.findByType<BaseAppModuleExtension>()?.run { lint.configureLint(project) }
    project.extensions.findByType<LibraryExtension>()?.run { lint.configureLint(project) }
  }
}

private fun Lint.configureLint(project: Project) {
  abortOnError = false
  checkReleaseBuilds = false
  warningsAsErrors = false
  disable.add("DialogFragmentCallbacksDetector")
  baseline = project.file("lint-baseline.xml")
}

/**
 * When the "slimTests" project property is provided, disable the unit test tasks on `release` build
 * type and `nonFree` product flavor to avoid running the same tests repeatedly in different build
 * variants.
 *
 * Examples: `./gradlew test -PslimTests` will run unit tests for `nonFreeDebug` and `debug` build
 * variants in Android App and Library projects, and all tests in JVM projects.
 */
private fun Project.configureSlimTests() {
  if (providers.gradleProperty(SLIM_TESTS_PROPERTY).isPresent) {
    // Disable unit test tasks on the release build type for Android Library projects
    extensions.findByType<LibraryAndroidComponentsExtension>()?.run {
      beforeVariants(selector().withBuildType("release")) {
        it.enableUnitTest = false
        it.enableAndroidTest = false
      }
    }

    // Disable unit test tasks on the release build type for Android Application projects.
    extensions.findByType<ApplicationAndroidComponentsExtension>()?.run {
      beforeVariants(selector().withBuildType("release")) {
        it.enableUnitTest = false
        it.enableAndroidTest = false
      }
    }
  }
}
