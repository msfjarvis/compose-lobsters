package dev.msfjarvis.aps.gradle

import com.android.build.api.dsl.TestExtension
import com.android.build.gradle.TestedExtension
import org.gradle.android.AndroidCacheFixPlugin
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.findByType

@Suppress("UnstableApiUsage")
class AndroidCommonPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.pluginManager.apply(AndroidCacheFixPlugin::class)
    project.extensions.findByType<TestedExtension>()?.run {
      setCompileSdkVersion(33)
      defaultConfig {
        minSdk = 26
        targetSdk = 33
      }

      sourceSets {
        named("main") { java.srcDirs("src/main/kotlin") }
        named("test") { java.srcDirs("src/test/kotlin") }
        named("androidTest") { java.srcDirs("src/androidTest/kotlin") }
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
    project.extensions.findByType<TestExtension>()?.run {
      compileSdk = 32
      defaultConfig {
        minSdk = 26
        targetSdk = 32
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
  }
}
