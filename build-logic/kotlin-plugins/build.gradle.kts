/*
 * Copyright © 2014-2021 The Android Password Store Authors. All Rights Reserved.
 * SPDX-License-Identifier: GPL-3.0-only
 */

import org.gradle.api.JavaVersion
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins { `kotlin-dsl` }

afterEvaluate {
  tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
  }

  tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
      jvmTarget = JavaVersion.VERSION_11.toString()
      freeCompilerArgs = freeCompilerArgs + "-Xsam-conversions=class"
    }
  }
}

gradlePlugin {
  plugins {
    register("kotlin-android") {
      id = "dev.msfjarvis.claw.kotlin-android"
      implementationClass = "dev.msfjarvis.aps.gradle.KotlinAndroidPlugin"
    }
    register("kotlin-common") {
      id = "dev.msfjarvis.claw.kotlin-common"
      implementationClass = "dev.msfjarvis.aps.gradle.KotlinCommonPlugin"
    }
    register("kotlin-kapt") {
      id = "dev.msfjarvis.claw.kotlin-kapt"
      implementationClass = "dev.msfjarvis.aps.gradle.KotlinKaptPlugin"
    }
    register("kotlin-library") {
      id = "dev.msfjarvis.claw.kotlin-library"
      implementationClass = "dev.msfjarvis.aps.gradle.KotlinLibraryPlugin"
    }
    register("spotless") {
      id = "dev.msfjarvis.claw.spotless"
      implementationClass = "dev.msfjarvis.aps.gradle.SpotlessPlugin"
    }
    register("versions") {
      id = "dev.msfjarvis.claw.versions"
      implementationClass = "dev.msfjarvis.aps.gradle.DependencyUpdatesPlugin"
    }
  }
}

dependencies {
  implementation(libs.build.agp)
  implementation(libs.build.kotlin.gradle)
  implementation(libs.build.kotlin.serialization)
  implementation(libs.build.spotless)
  implementation(libs.build.vcu)
  implementation(libs.build.versions)
}
