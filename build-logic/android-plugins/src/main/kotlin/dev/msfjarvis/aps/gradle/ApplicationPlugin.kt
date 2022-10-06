@file:Suppress("UnstableApiUsage")

package dev.msfjarvis.aps.gradle

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import dev.msfjarvis.aps.gradle.signing.configureBuildSigning
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

@Suppress("Unused")
class ApplicationPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    project.pluginManager.apply(AppPlugin::class)
    project.pluginManager.apply(AndroidCommonPlugin::class)
    project.extensions.getByType<BaseAppModuleExtension>().run {
      adbOptions.installOptions("--user 0")

      dependenciesInfo {
        includeInBundle = false
        includeInApk = false
      }

      buildFeatures {
        viewBinding = true
        buildConfig = true
      }

      buildTypes {
        named("release") {
          isMinifyEnabled = true
          setProguardFiles(
            listOf(
              "proguard-android-optimize.pro",
              "proguard-rules.pro",
              "proguard-rules-missing-classes.pro",
            )
          )
        }
        named("debug") {
          applicationIdSuffix = ".debug"
          versionNameSuffix = "-debug"
          isMinifyEnabled = false
        }
      }

      project.configureBuildSigning()
    }
  }
}
