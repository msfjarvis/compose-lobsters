@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.library)
  alias(libs.plugins.sqldelight)
}

kotlin {
  android()
  jvm("desktop") {
    compilations.all {
      kotlinOptions.jvmTarget = "11"
      kotlinOptions.freeCompilerArgs =
        kotlinOptions.freeCompilerArgs + listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
  }
  sourceSets["androidMain"].apply { dependencies { implementation(libs.sqldelight.androidDriver) } }
  sourceSets["desktopMain"].apply { dependencies { implementation(libs.sqldelight.jvmDriver) } }
  sourceSets["desktopTest"].apply {
    dependencies {
      implementation(libs.kotlin.coroutines.core)
      implementation(kotlin("test-junit"))
    }
  }
}

android {
  compileSdk = 31
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  defaultConfig {
    minSdk = 23
    targetSdk = 31
    consumerProguardFiles("consumer-rules.pro")
  }
}

sqldelight {
  database("LobstersDatabase") {
    packageName = "dev.msfjarvis.claw.database"
    sourceFolders = listOf("sqldelight")
    schemaOutputDirectory = file("src/commonMain/sqldelight/databases")
    verifyMigrations = true
  }
}
