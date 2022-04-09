@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

plugins {
  kotlin("multiplatform")
  kotlin("plugin.serialization")
  id("dev.msfjarvis.claw.kotlin-common")
  id("dev.msfjarvis.claw.android-library")
}

kotlin {
  android()
  jvm()
  sourceSets["androidMain"].apply {
    dependsOn(sourceSets["androidAndroidTestRelease"])
    dependsOn(sourceSets["androidTestFixtures"])
    dependsOn(sourceSets["androidTestFixturesDebug"])
    dependsOn(sourceSets["androidTestFixturesRelease"])
  }
  sourceSets["commonMain"].apply {
    dependencies { implementation(libs.kotlinx.serialization.core) }
  }
}

android { sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml") }
