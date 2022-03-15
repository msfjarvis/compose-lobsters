@file:OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.BuiltArtifactsLoader
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.Properties
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

plugins {
  id("org.jetbrains.compose")
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
  id("dagger.hilt.android.plugin")
}

@CacheableTask
abstract class CollectApksTask : DefaultTask() {

  @get:InputFiles @get:PathSensitive(PathSensitivity.NONE) abstract val apkFolder: DirectoryProperty
  @get:Input abstract val variantName: Property<String>
  @get:Internal abstract val builtArtifactsLoader: Property<BuiltArtifactsLoader>
  @get:OutputDirectory abstract val outputDirectory: DirectoryProperty

  @TaskAction
  fun taskAction() {
    val outputDir = outputDirectory.asFile.get()
    outputDir.mkdirs()
    val builtArtifacts =
      builtArtifactsLoader.get().load(apkFolder.get()) ?: throw RuntimeException("Cannot load APKs")
    builtArtifacts.elements.forEach { artifact ->
      Files.copy(
        Paths.get(artifact.outputFile),
        outputDir.resolve("Claw-${variantName.get()}-${artifact.versionName}.apk").toPath(),
        StandardCopyOption.REPLACE_EXISTING,
      )
    }
  }
}

dependencies {
  kapt(libs.dagger.hilt.compiler)
  implementation(projects.api)
  implementation(projects.common)
  implementation(projects.database)
  implementation(compose.material3)
  implementation(libs.accompanist.insets)
  implementation(libs.accompanist.swiperefresh)
  implementation(libs.accompanist.sysuicontroller)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.core.splashscreen)
  implementation(libs.androidx.lifecycle.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.paging.compose)
  implementation(libs.copydown)
  implementation(libs.dagger.hilt.android)
  implementation(libs.sqldelight.extensions.coroutines)
  implementation(libs.kotlin.coroutines.core)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.retrofit.kotlinxSerializationConverter) { isTransitive = false }
}

android {
  compileSdk = 31
  defaultConfig {
    applicationId = "dev.msfjarvis.claw.android"
    minSdk = 23
    targetSdk = 31
    versionCode = 1
    versionName = "1.0"
  }
  val keystoreConfigFile = rootProject.layout.projectDirectory.file("keystore.properties")
  if (keystoreConfigFile.asFile.exists()) {
    val contents = providers.fileContents(keystoreConfigFile).asText
    val keystoreProperties = Properties()
    keystoreProperties.load(contents.get().byteInputStream())
    signingConfigs {
      register("release") {
        keyAlias = keystoreProperties["keyAlias"] as String
        keyPassword = keystoreProperties["keyPassword"] as String
        storeFile = rootProject.file(keystoreProperties["storeFile"] as String)
        storePassword = keystoreProperties["storePassword"] as String
      }
    }
    buildTypes.all { signingConfig = signingConfigs.getByName("release") }
  }
  buildFeatures { buildConfig = true }
  buildTypes {
    named("release") {
      isMinifyEnabled = false
      setProguardFiles(listOf("proguard-android-optimize.txt", "proguard-rules.pro"))
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  dependenciesInfo {
    includeInBundle = false
    includeInApk = false
  }
}

androidComponents {
  onVariants { variant ->
    project.tasks.register<CollectApksTask>("collect${variant.name.capitalize()}Apks") {
      variantName.set(variant.name)
      apkFolder.set(variant.artifacts.get(SingleArtifact.APK))
      builtArtifactsLoader.set(variant.artifacts.getBuiltArtifactsLoader())
      outputDirectory.set(project.layout.projectDirectory.dir("outputs"))
    }
  }
}
