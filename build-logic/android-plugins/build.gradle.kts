/*
 * Copyright © 2022 Harsh Shandilya.
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
      implementationClass = "dev.msfjarvis.aps.gradle.ApplicationPlugin"
    }
    register("android-common") {
      id = "dev.msfjarvis.claw.android-common"
      implementationClass = "dev.msfjarvis.aps.gradle.AndroidCommonPlugin"
    }
    register("android-library") {
      id = "dev.msfjarvis.claw.android-library"
      implementationClass = "dev.msfjarvis.aps.gradle.LibraryPlugin"
    }
    register("rename-artifacts") {
      id = "dev.msfjarvis.claw.rename-artifacts"
      implementationClass = "dev.msfjarvis.aps.gradle.RenameArtifactsPlugin"
    }
    register("versioning") {
      id = "dev.msfjarvis.claw.versioning-plugin"
      implementationClass = "dev.msfjarvis.aps.gradle.versioning.VersioningPlugin"
    }
  }
}

dependencies {
  implementation(libs.build.agp)
  implementation(libs.build.cachefix)
  implementation(libs.build.semver)
}
