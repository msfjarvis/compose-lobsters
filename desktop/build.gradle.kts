import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
  kotlin("multiplatform")
  id("org.jetbrains.compose") version "1.0.0-alpha4-build366"
}

group = "dev.msfjarvis.claw"

version = "1.0"

kotlin {
  jvm { compilations.all { kotlinOptions.jvmTarget = "11" } }
  sourceSets["jvmMain"].apply {
    dependencies {
      implementation(projects.api)
      implementation(projects.common)
      implementation(libs.aurora.component)
      implementation(libs.aurora.skin)
      implementation(libs.aurora.window)
      implementation(libs.kotlinx.serialization.json)
      implementation(libs.multiplatform.paging)
      implementation(libs.retrofit.lib)
      implementation(libs.retrofit.kotlinxSerializationConverter)
      implementation(compose.desktop.currentOs)
    }
  }
}

compose.desktop {
  application {
    mainClass = "MainKt"
    jvmArgs += listOf("-Xmx1G")
    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      packageName = "Claw"
      packageVersion = "1.0.0"
      description = "Desktop client for lobste.rs link aggregation site"
      copyright = "© 2021 Harsh Shandilya. All rights reserved."
      vendor = "Harsh Shandilya"
      includeAllModules = false
    }
  }
}
