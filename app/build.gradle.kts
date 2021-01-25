plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
  kotlin("plugin.serialization") version "1.4.21"
  id("dagger.hilt.android.plugin")
  `versioning-plugin`
  `lobsters-plugin`
  `core-library-desugaring`
}

android {
  defaultConfig {
    applicationId = "dev.msfjarvis.lobsters"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    javaCompileOptions.annotationProcessorOptions {
      argument("room.schemaLocation", "${projectDir}/schemas")
    }
  }

  buildFeatures.compose = true

  composeOptions {
    kotlinCompilerVersion = "1.4.21"
    kotlinCompilerExtensionVersion = Dependencies.COMPOSE_VERSION
  }
}

dependencies {

  kapt(Dependencies.AndroidX.Hilt.daggerCompiler)
  kapt(Dependencies.AndroidX.Room.compiler)
  kapt(Dependencies.ThirdParty.Roomigrant.compiler)
  implementation(project(":model"))
  implementation(Dependencies.AndroidX.appCompat)
  implementation(Dependencies.AndroidX.browser)
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
  implementation(Dependencies.AndroidX.Lifecycle.runtimeKtx)
  implementation(Dependencies.AndroidX.Lifecycle.viewmodelKtx)
  implementation(Dependencies.AndroidX.Room.runtime)
  implementation(Dependencies.AndroidX.Room.ktx)
  implementation(Dependencies.Kotlin.Coroutines.android)
  implementation(Dependencies.ThirdParty.accompanist)
  implementation(Dependencies.ThirdParty.Moshi.lib)
  implementation(Dependencies.ThirdParty.Roomigrant.runtime)
  testImplementation(Dependencies.Testing.junit)
  androidTestImplementation(Dependencies.Testing.daggerHilt)
  androidTestImplementation(Dependencies.Testing.uiTest)
}
