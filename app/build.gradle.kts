plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
  id("dagger.hilt.android.plugin")
  id("org.jetbrains.compose") version "0.4.0-build178"
  id("shot")
  `versioning-plugin`
  `lobsters-plugin`
  `core-library-desugaring`
}

repositories { maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") }

android {
  defaultConfig {
    applicationId = "dev.msfjarvis.lobsters"
    testInstrumentationRunner = "com.karumi.shot.ShotTestRunner"
  }
}

dependencies {
  kapt(Dependencies.AndroidX.Hilt.daggerCompiler)
  implementation(project(":api"))
  implementation(project(":common"))
  implementation(project(":database"))
  implementation(compose.foundation)
  implementation(compose.material)
  implementation(compose.runtime)
  implementation(compose.ui)
  implementation(Dependencies.AndroidX.appCompat)
  implementation(Dependencies.AndroidX.browser)
  implementation(Dependencies.AndroidX.datastore)
  implementation(Dependencies.AndroidX.Compose.activity)
  implementation(Dependencies.AndroidX.Compose.lifecycleViewModel)
  implementation(Dependencies.AndroidX.Compose.navigation)
  implementation(Dependencies.AndroidX.Compose.paging)
  implementation(Dependencies.AndroidX.Compose.uiTooling)
  implementation(Dependencies.AndroidX.Hilt.dagger)
  implementation(Dependencies.AndroidX.Lifecycle.runtimeKtx)
  implementation(Dependencies.AndroidX.Lifecycle.viewmodelKtx)
  implementation(Dependencies.Kotlin.Coroutines.android)
  implementation(Dependencies.ThirdParty.accompanistCoil)
  implementation(Dependencies.ThirdParty.accompanistFlow)
  implementation(Dependencies.ThirdParty.Moshi.lib)
  implementation(Dependencies.ThirdParty.Retrofit.moshi)
  implementation(Dependencies.ThirdParty.SQLDelight.androidDriver)
  testImplementation(kotlin("test-junit"))
  androidTestImplementation(kotlin("test-junit"))
}
