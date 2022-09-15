@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.pushingpixels.aurora.tools.svgtranscoder.gradle.TranscodeTask

plugins {
  kotlin("android")
  id("dev.msfjarvis.claw.kotlin-common")
  id("dev.msfjarvis.claw.android-library")
  alias(libs.plugins.aurora.svg.transcoder)
}

androidComponents { beforeVariants { it.enableUnitTest = false } }

val transcodeTask =
  tasks.register<TranscodeTask>("transcodeSvgs") {
    inputDirectory = file("svgs")
    outputDirectory = file("src/gen/kotlin/dev/msfjarvis/claw/common/res/clawicons")
    outputPackageName = "dev.msfjarvis.claw.common.res.clawicons"
  }

tasks.withType<KotlinCompile>().configureEach { dependsOn(transcodeTask) }

dependencies {
  api(libs.napier)
  implementation(libs.androidx.compose.animation)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.ui.text)
  implementation(projects.database)
  implementation(projects.model)
  implementation(libs.accompanist.flowlayout)
  implementation(libs.androidx.browser)
  implementation(libs.coil.compose)
  implementation(libs.compose.richtext.markdown)
  implementation(libs.compose.richtext.material3)
  implementation(libs.compose.richtext.ui)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.kotlinx.datetime)
  testImplementation(kotlin("test-junit"))
  testImplementation(libs.testparameterinjector)
}

android {
  buildFeatures {
    androidResources = true
    compose = true
  }
  composeOptions {
    useLiveLiterals = false
    kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
  }
  namespace = "dev.msfjarvis.claw.common"
  sourceSets { getByName("main") { kotlin.srcDir("src/gen/kotlin") } }
}
