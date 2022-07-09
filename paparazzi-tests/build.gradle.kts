@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  id("dev.msfjarvis.claw.android-library")
  id("dev.msfjarvis.claw.kotlin-android")
  alias(libs.plugins.paparazzi)
}

androidComponents { beforeVariants(selector().all()) { it.enable = it.buildType == "debug" } }

android {
  namespace = "dev.msfjarvis.claw.android.tests"
  buildFeatures { compose = true }
  composeOptions {
    useLiveLiterals = false
    kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
  }
}

dependencies {
  testImplementation(kotlin("test-junit"))
  testImplementation(libs.kotlinx.datetime)
  testImplementation(libs.testparameterinjector)
  testImplementation(projects.common)
}

tasks.withType<Test>().configureEach {
  javaLauncher.set(javaToolchains.launcherFor { languageVersion.set(JavaLanguageVersion.of(11)) })
}
