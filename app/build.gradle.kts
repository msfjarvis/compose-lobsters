plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
  id("dagger.hilt.android.plugin")
  id("org.jetbrains.compose")
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
}

dependencies {
  kapt(libs.androidx.hilt.daggerCompiler)
  implementation(projects.api)
  implementation(projects.common)
  implementation(projects.database)
  implementation(compose.foundation)
  implementation(compose.material)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.browser)
  implementation(libs.androidx.datastore)
  implementation(libs.androidx.compose.activity)
  implementation(libs.androidx.compose.lifecycleViewModel)
  implementation(libs.androidx.compose.navigation)
  implementation(libs.androidx.compose.paging)
  implementation(libs.androidx.compose.uiTooling)
  implementation(libs.androidx.hilt.dagger)
  implementation(libs.bundles.androidxLifecycle)
  implementation(libs.kotlin.coroutines.android)
  implementation(libs.thirdparty.accompanist.coil)
  implementation(libs.thirdparty.accompanist.flow)
  implementation(libs.thirdparty.accompanist.swiperefresh)
  implementation(libs.thirdparty.moshi.lib)
  implementation(libs.thirdparty.moshix.metadatareflect)
  implementation(libs.thirdparty.retrofit.moshiConverter)
  testImplementation(libs.testing.kotlintest.junit)
  androidTestImplementation(libs.testing.kotlintest.junit)
}
