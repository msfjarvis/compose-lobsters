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
  sourceSets["androidMain"].apply {
    dependencies { implementation(libs.sqldelight.androidDriver) }
    dependsOn(sourceSets["androidAndroidTestRelease"])
    dependsOn(sourceSets["androidTestFixtures"])
    dependsOn(sourceSets["androidTestFixturesDebug"])
    dependsOn(sourceSets["androidTestFixturesRelease"])
  }
  sourceSets["desktopMain"].apply { dependencies { implementation(libs.sqldelight.jvmDriver) } }
  sourceSets["desktopTest"].apply {
    dependencies {
      implementation(libs.kotlin.coroutines.core)
      implementation(kotlin("test-junit"))
    }
  }
}

android { sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml") }

sqldelight {
  database("LobstersDatabase") {
    packageName = "dev.msfjarvis.claw.database"
    sourceFolders = listOf("sqldelight")
    schemaOutputDirectory = file("src/commonMain/sqldelight/databases")
    verifyMigrations = true
  }
}
