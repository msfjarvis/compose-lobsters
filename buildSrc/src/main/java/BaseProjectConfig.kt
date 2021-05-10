/*
 * Copyright © 2014-2020 The Android Password Store Authors. All Rights Reserved.
 * SPDX-License-Identifier: GPL-3.0-only
 */

import com.android.build.gradle.TestedExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.api.tasks.wrapper.Wrapper
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Configure root project. Note that classpath dependencies still need to be defined in the
 * `buildscript` block in the top-level build.gradle.kts file.
 */
internal fun Project.configureForRootProject() {
  tasks.withType<Wrapper> {
    gradleVersion = "7.0.1"
    distributionType = Wrapper.DistributionType.ALL
    distributionSha256Sum = "ca42877db3519b667cd531c414be517b294b0467059d401e7133f0e55b9bf265"
  }
}

/** Configure all projects including the root project */
internal fun Project.configureForAllProjects() {
  repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") {
      name = "JetBrains Space Compose Repository"
      content {
        includeGroup("org.jetbrains.compose.animation")
        includeGroup("org.jetbrains.compose.compiler")
        includeGroup("org.jetbrains.compose.desktop")
        includeGroup("org.jetbrains.compose.foundation")
        includeGroup("org.jetbrains.compose.material")
        includeGroup("org.jetbrains.compose.runtime")
        includeGroup("org.jetbrains.compose.ui")
        includeGroup("org.jetbrains.skiko")
      }
    }
  }
  tasks.withType<KotlinCompile> {
    kotlinOptions {
      jvmTarget = JavaVersion.VERSION_1_8.toString()
      freeCompilerArgs = freeCompilerArgs + additionalCompilerArgs
      languageVersion = "1.4"
    }
  }
  tasks.withType<Test> {
    maxParallelForks = Runtime.getRuntime().availableProcessors() * 2
    testLogging { events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED) }
  }
}

/** Apply configuration options for Android Application projects. */
@Suppress("UnstableApiUsage")
internal fun BaseAppModuleExtension.configureAndroidApplicationOptions(project: Project) {
  val minifySwitch =
    project.providers.environmentVariable("DISABLE_MINIFY").forUseAtConfigurationTime()
  project.tasks.withType<KotlinCompile> {
    kotlinOptions {
      freeCompilerArgs =
        freeCompilerArgs +
          listOf(
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xopt-in=androidx.compose.material.ExperimentalMaterialApi"
          )
    }
  }
  adbOptions.installOptions("--user 0")
  buildTypes {
    named("release") {
      isMinifyEnabled = !minifySwitch.isPresent
      proguardFiles("proguard-android-optimize.txt", "proguard-rules.pro")
    }
    named("debug") {
      applicationIdSuffix = ".debug"
      versionNameSuffix = "-debug"
      isMinifyEnabled = false
    }
  }
}

/** Apply baseline configurations for all Android projects (Application and Library). */
@Suppress("UnstableApiUsage")
internal fun TestedExtension.configureCommonAndroidOptions() {
  compileSdkVersion(30)

  defaultConfig {
    minSdk = 23
    targetSdk = 30
  }

  packagingOptions.resources.excludes.addAll(
    setOf(
      "**/*.version",
      "**/*.txt",
      "**/*.kotlin_module",
      "**/plugin.properties",
      "META-INF/AL2.0",
      "META-INF/LGPL2.1"
    )
  )

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  testOptions.animationsDisabled = true
}
