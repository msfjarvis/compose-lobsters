import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
  kotlin("multiplatform")
  id("org.jetbrains.compose")
  `lobsters-plugin`
}

kotlin {
  jvm()
  sourceSets {
    named("jvmMain") {
      dependencies {
        implementation(compose.desktop.currentOs)
        implementation(projects.api)
        implementation(projects.common)
        implementation(projects.database)
        implementation(compose.material)
        implementation(libs.kotlin.coroutines.jvm)
        implementation(libs.thirdparty.kamel)
        implementation(libs.thirdparty.retrofit.moshiConverter)
      }
    }
  }
}

compose.desktop {
  application {
    mainClass = "dev.msfjarvis.lobsters.ui.Main"
    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      packageName = "Claw"
      packageVersion = "1.0.0"
    }
  }
}
