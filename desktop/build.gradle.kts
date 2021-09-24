import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
  kotlin("multiplatform")
  id("org.jetbrains.compose") version "1.0.0-alpha4-build348"
}

group = "dev.msfjarvis.claw"

version = "1.0"

kotlin {
  jvm { compilations.all { kotlinOptions.jvmTarget = "11" } }
  sourceSets {
    val jvmMain by getting {
      dependencies {
        implementation(projects.common)
        implementation(libs.aurora.component)
        implementation(libs.aurora.skin)
        implementation(libs.aurora.window)
        implementation(compose.desktop.currentOs)
      }
    }
    val jvmTest by getting
  }
}

compose.desktop {
  application {
    mainClass = "MainKt"
    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      packageName = "jvm"
      packageVersion = "1.0.0"
    }
  }
}
