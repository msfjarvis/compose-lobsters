@file:OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.pushingpixels.aurora.tools.svgtranscoder.gradle.TranscodeTask

plugins {
  kotlin("multiplatform")
  alias(libs.plugins.compose)
  id("dev.msfjarvis.claw.kotlin-common")
  id("dev.msfjarvis.claw.android-library")
  alias(libs.plugins.aurora.svg.transcoder)
}

val transcodeTask =
  tasks.register<TranscodeTask>("transcodeSvgs") {
    inputDirectory = file("src/commonMain/svgs/")
    outputDirectory = file("src/gen/kotlin/dev/msfjarvis/claw/common/res/clawicons")
    outputPackageName = "dev.msfjarvis.claw.common.res.clawicons"
    transcode()
  }

tasks.withType<KotlinCompile>().configureEach { dependsOn(transcodeTask) }

kotlin {
  android()
  jvm("desktop")
  sourceSets["commonMain"].apply {
    dependencies {
      api(compose.runtime)
      api(compose.foundation)
      api(compose.material)
      api(compose.material3)
      api(projects.database)
      api(projects.model)
      api(libs.napier)
      implementation(libs.moko.paging)
      implementation(libs.kotlin.coroutines.core)
      implementation(libs.compose.richtext.markdown)
      implementation(libs.compose.richtext.material)
      implementation(libs.compose.richtext.ui)
    }
    kotlin.srcDir("src/gen/kotlin/")
  }
  sourceSets["androidMain"].apply {
    dependencies {
      implementation(libs.androidx.browser)
      implementation(libs.coil.compose)
    }
    dependsOn(sourceSets["androidAndroidTestRelease"])
  }
  sourceSets["desktopMain"].apply {
    resources.srcDir("src/androidMain/res/drawable/")
    dependencies { implementation(libs.kamel.image) }
  }
}

android {
  buildFeatures { androidResources = true }
  namespace = "dev.msfjarvis.claw.common"
  sourceSets["main"].apply {
    manifest.srcFile("src/androidMain/AndroidManifest.xml")
    res.srcDirs("src/commonMain/resources")
  }
}
