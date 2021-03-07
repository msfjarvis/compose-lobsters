plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
  id("dagger.hilt.android.plugin")
  id("shot")
  `versioning-plugin`
  `lobsters-plugin`
  `core-library-desugaring`
}

android {
  defaultConfig {
    applicationId = "dev.msfjarvis.lobsters"
    testInstrumentationRunner = "com.karumi.shot.ShotTestRunner"
  }

  buildFeatures.compose = true

  composeOptions {
    kotlinCompilerExtensionVersion = Dependencies.COMPOSE_VERSION
  }
}

dependencies {

  kapt(Dependencies.AndroidX.Hilt.daggerCompiler)
  implementation(project(":api"))
  implementation(project(":database"))
  implementation(Dependencies.AndroidX.appCompat)
  implementation(Dependencies.AndroidX.browser)
  implementation(Dependencies.AndroidX.Compose.activity)
  implementation(Dependencies.AndroidX.Compose.compiler)
  implementation(Dependencies.AndroidX.Compose.foundation)
  implementation(Dependencies.AndroidX.Compose.foundationLayout)
  implementation(Dependencies.AndroidX.Compose.lifecycleViewModel)
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
  implementation(Dependencies.Kotlin.Coroutines.android)
  implementation(Dependencies.ThirdParty.accompanist)
  implementation(Dependencies.ThirdParty.composeFlowLayout)
  implementation(Dependencies.ThirdParty.Moshi.lib)
  implementation(Dependencies.ThirdParty.pullToRefresh)
  implementation(Dependencies.ThirdParty.Retrofit.moshi)
  implementation(Dependencies.ThirdParty.SQLDelight.androidDriver)
  testImplementation(Dependencies.Testing.junit)
  androidTestImplementation(Dependencies.AndroidX.Compose.activity)
  androidTestImplementation(Dependencies.Testing.daggerHilt)
  androidTestImplementation(Dependencies.Testing.AndroidX.Compose.uiTestJunit4)
}
