/*
 * Copyright © 2022-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
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
    register("android-application") {
      id = "dev.msfjarvis.claw.android-application"
      implementationClass = "dev.msfjarvis.claw.gradle.ApplicationPlugin"
    }
    register("android-common") {
      id = "dev.msfjarvis.claw.android-common"
      implementationClass = "dev.msfjarvis.claw.gradle.AndroidCommonPlugin"
    }
    register("android-library") {
      id = "dev.msfjarvis.claw.android-library"
      implementationClass = "dev.msfjarvis.claw.gradle.LibraryPlugin"
    }
    register("kotlin-android") {
      id = "dev.msfjarvis.claw.kotlin-android"
      implementationClass = "dev.msfjarvis.claw.gradle.KotlinAndroidPlugin"
    }
    register("kotlin-common") {
      id = "dev.msfjarvis.claw.kotlin-common"
      implementationClass = "dev.msfjarvis.claw.gradle.KotlinCommonPlugin"
    }
    register("kotlin-jvm") {
      id = "dev.msfjarvis.claw.kotlin-jvm"
      implementationClass = "dev.msfjarvis.claw.gradle.KotlinJvmPlugin"
    }
    register("kotlin-kapt") {
      id = "dev.msfjarvis.claw.kotlin-kapt"
      implementationClass = "dev.msfjarvis.claw.gradle.KotlinKaptPlugin"
    }
    register("rename-artifacts") {
      id = "dev.msfjarvis.claw.rename-artifacts"
      implementationClass = "dev.msfjarvis.claw.gradle.RenameArtifactsPlugin"
    }
    register("sentry") {
      id = "dev.msfjarvis.claw.sentry"
      implementationClass = "dev.msfjarvis.claw.gradle.SentryPlugin"
    }
    register("spotless") {
      id = "dev.msfjarvis.claw.spotless"
      implementationClass = "dev.msfjarvis.claw.gradle.SpotlessPlugin"
    }
    register("versioning") {
      id = "dev.msfjarvis.claw.versioning-plugin"
      implementationClass = "dev.msfjarvis.claw.gradle.versioning.VersioningPlugin"
    }
    register("versions") {
      id = "dev.msfjarvis.claw.versions"
      implementationClass = "dev.msfjarvis.claw.gradle.DependencyUpdatesPlugin"
    }
  }
}

dependencies {
  implementation(libs.build.agp)
  implementation(libs.build.cachefix)
  implementation(libs.build.kotlin.gradle)
  implementation(libs.build.semver)
  implementation(libs.build.sentry)
  implementation(libs.build.spotless)
  implementation(libs.build.vcu)
  implementation(libs.build.versions)
}
