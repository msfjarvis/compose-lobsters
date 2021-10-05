import java.util.Properties

plugins {
  id("org.jetbrains.compose") version "1.0.0-alpha4-build366"
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
  id("dagger.hilt.android.plugin")
}

dependencies {
  kapt(libs.dagger.hilt.compiler)
  implementation(projects.api)
  implementation(projects.common)
  implementation(projects.database)
  implementation(libs.accompanist.insets)
  implementation(libs.accompanist.swiperefresh)
  implementation(libs.accompanist.sysuicontroller)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.coreKtx)
  implementation(libs.androidx.lifecycle.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.paging.compose)
  implementation(libs.compose.richtext.markdown)
  implementation(libs.compose.richtext.material)
  implementation(libs.compose.richtext.ui)
  implementation(libs.copydown)
  implementation(libs.dagger.hilt.android)
  implementation(libs.sqldelight.extensions.coroutines)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.retrofit.kotlinxSerializationConverter) { isTransitive = false }
}

android {
  compileSdk = 31
  defaultConfig {
    applicationId = "dev.msfjarvis.claw.android"
    minSdk = 23
    targetSdk = 31
    versionCode = 1
    versionName = "1.0"
  }
  val keystoreConfigFile = rootProject.layout.projectDirectory.file("keystore.properties")
  if (keystoreConfigFile.asFile.exists()) {
    val contents = providers.fileContents(keystoreConfigFile).asText.forUseAtConfigurationTime()
    val keystoreProperties = Properties()
    keystoreProperties.load(contents.get().byteInputStream())
    signingConfigs {
      register("release") {
        keyAlias = keystoreProperties["keyAlias"] as String
        keyPassword = keystoreProperties["keyPassword"] as String
        storeFile = rootProject.file(keystoreProperties["storeFile"] as String)
        storePassword = keystoreProperties["storePassword"] as String
      }
    }
    buildTypes.all { signingConfig = signingConfigs.getByName("release") }
  }
  buildTypes {
    getByName("release") {
      isMinifyEnabled = true
      proguardFiles("proguard-android-optimize.txt", "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
}
