import java.util.Properties

plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
  kotlin("plugin.serialization") version "1.4.21"
  id("dagger.hilt.android.plugin")
  `lobsters-plugin`
  `core-library-desugaring`
}

val keystorePropertiesFile = rootProject.file("keystore.properties")

android {
  adbOptions.installOptions("--user 0")
  defaultConfig {
    applicationId = "dev.msfjarvis.lobsters"
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    javaCompileOptions.annotationProcessorOptions {
      argument("room.schemaLocation", "${projectDir}/schemas")
    }
  }

  buildTypes {
    named("release") {
      isMinifyEnabled = true
      setProguardFiles(listOf("proguard-android-optimize.txt", "proguard-rules.pro"))
    }
  }

  buildFeatures.compose = true

  composeOptions {
    kotlinCompilerVersion = "1.4.21"
    kotlinCompilerExtensionVersion = Dependencies.COMPOSE_VERSION
  }

  if (keystorePropertiesFile.exists()) {
    val keystoreProperties = Properties()
    keystoreProperties.load(keystorePropertiesFile.inputStream())
    signingConfigs {
      register("release") {
        keyAlias = keystoreProperties["keyAlias"] as String
        keyPassword = keystoreProperties["keyPassword"] as String
        storeFile = rootProject.file(keystoreProperties["storeFile"] as String)
        storePassword = keystoreProperties["storePassword"] as String
      }
    }
    listOf("release", "debug").map {
      buildTypes.getByName(it).signingConfig = signingConfigs.getByName(it)
    }
  }
}

dependencies {

  kapt(Dependencies.AndroidX.Hilt.daggerCompiler)
  kapt(Dependencies.AndroidX.Hilt.daggerHiltCompiler)
  kapt(Dependencies.AndroidX.Room.compiler)
  kapt(Dependencies.ThirdParty.Roomigrant.compiler)
  implementation(project(":model"))
  implementation(Dependencies.AndroidX.activityKtx)
  implementation(Dependencies.AndroidX.appCompat)
  implementation(Dependencies.AndroidX.browser)
  implementation(Dependencies.AndroidX.coreKtx)
  implementation(Dependencies.AndroidX.material)
  implementation(Dependencies.AndroidX.Compose.compiler)
  implementation(Dependencies.AndroidX.Compose.foundation)
  implementation(Dependencies.AndroidX.Compose.foundationLayout)
  implementation(Dependencies.AndroidX.Compose.material)
  implementation(Dependencies.AndroidX.Compose.navigation)
  implementation(Dependencies.AndroidX.Compose.paging)
  implementation(Dependencies.AndroidX.Compose.runtime)
  implementation(Dependencies.AndroidX.Compose.ui)
  implementation(Dependencies.AndroidX.Compose.uiTooling)
  implementation(Dependencies.AndroidX.Compose.uiUnit)
  implementation(Dependencies.AndroidX.Hilt.dagger)
  implementation(Dependencies.AndroidX.Hilt.hiltLifecycleViewmodel)
  implementation(Dependencies.AndroidX.Lifecycle.runtimeKtx)
  implementation(Dependencies.AndroidX.Lifecycle.viewmodelKtx)
  implementation(Dependencies.AndroidX.Room.runtime)
  implementation(Dependencies.AndroidX.Room.ktx)
  implementation(Dependencies.Kotlin.Coroutines.android)
  implementation(Dependencies.Kotlin.Serialization.json)
  implementation(Dependencies.ThirdParty.accompanist)
  implementation(Dependencies.ThirdParty.customtabs)
  implementation(Dependencies.ThirdParty.Roomigrant.runtime)
  testImplementation(Dependencies.Testing.junit)
  testImplementation(Dependencies.Kotlin.Ktor.clientTest)
  androidTestImplementation(Dependencies.Testing.daggerHilt)
  androidTestImplementation(Dependencies.Testing.uiTest)
}
