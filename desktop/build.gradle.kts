import org.jetbrains.compose.compose

plugins {
  kotlin("jvm")
  id("org.jetbrains.compose") version "0.4.0-build177"
  `lobsters-plugin`
}

repositories {
  maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
  implementation(project(":api"))
  implementation(project(":common"))
  implementation(project(":database"))
  implementation(compose.desktop.currentOs)
  implementation(compose.runtime)
  implementation(compose.material)
  implementation(Dependencies.Kotlin.Coroutines.jvmCore)
  implementation(Dependencies.ThirdParty.kamel)
  implementation(Dependencies.ThirdParty.Retrofit.moshi)
}

compose.desktop {
  application {
    mainClass = "dev.msfjarvis.lobsters.ui.Main"
  }
}
