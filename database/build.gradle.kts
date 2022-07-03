@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

plugins {
  kotlin("multiplatform")
  alias(libs.plugins.sqldelight)
  id("dev.msfjarvis.claw.kotlin-common")
  id("dev.msfjarvis.claw.android-library")
}

kotlin {
  android()
  jvm("desktop")
  sourceSets["androidMain"].apply { dependencies { implementation(libs.sqldelight.androidDriver) } }
  sourceSets["commonMain"].apply {
    dependencies { implementation(libs.sqldelight.primitiveAdapters) }
  }
  sourceSets["desktopMain"].apply { dependencies { implementation(libs.sqldelight.jvmDriver) } }
  sourceSets["desktopTest"].apply {
    dependencies {
      implementation(libs.kotlinx.coroutines.core)
      implementation(kotlin("test-junit"))
    }
  }
}

android {
  namespace = "dev.msfjarvis.claw.database"
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
}

sqldelight {
  database("LobstersDatabase") {
    packageName = "dev.msfjarvis.claw.database"
    sourceFolders = listOf("sqldelight")
    schemaOutputDirectory = file("src/commonMain/sqldelight/databases")
    verifyMigrations = true
  }
}
