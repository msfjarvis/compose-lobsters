import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
  kotlin("multiplatform")
  id("org.jetbrains.compose") version Dependencies.JB_COMPOSE_VERSION
  `lobsters-plugin`
}

repositories { maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") }

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
        implementation(Dependencies.Kotlin.Coroutines.jvmCore)
        implementation(Dependencies.ThirdParty.kamel)
        implementation(Dependencies.ThirdParty.Retrofit.moshi)
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
