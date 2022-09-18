@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

plugins {
  kotlin("android")
  id("dev.msfjarvis.claw.kotlin-common")
  id("dev.msfjarvis.claw.android-library")
}

androidComponents { beforeVariants { it.enableUnitTest = false } }

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
}
