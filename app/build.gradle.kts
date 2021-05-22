plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
  id("dagger.hilt.android.plugin")
  id("org.jetbrains.compose")
  `versioning-plugin`
  `lobsters-plugin`
  `core-library-desugaring`
}

android { defaultConfig { applicationId = "dev.msfjarvis.lobsters" } }

dependencies {
  // We explicitly make all AndroidX Compose-depending artifacts exclude
  // all the Google coordinates so that the same classes can be provided
  // by JetBrains' repository.
  fun composeDependency(notation: Provider<MinimalExternalModuleDependency>) {
    implementation(notation) {
      exclude(group = "androidx.compose.animation")
      exclude(group = "androidx.compose.compiler")
      exclude(group = "androidx.compose.foundation")
      exclude(group = "androidx.compose.material")
      exclude(group = "androidx.compose.runtime")
      exclude(group = "androidx.compose.ui")
    }
  }
  kapt(libs.androidx.hilt.daggerCompiler)
  implementation(projects.api)
  implementation(projects.common)
  implementation(projects.database)
  implementation(compose.animation)
  implementation(compose.foundation)
  implementation(compose.material)
  implementation(compose.ui)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.browser)
  implementation(libs.androidx.datastore)
  composeDependency(libs.androidx.compose.activity)
  composeDependency(libs.androidx.compose.lifecycleViewModel)
  composeDependency(libs.androidx.compose.navigation)
  composeDependency(libs.androidx.compose.paging)
  composeDependency(libs.androidx.compose.uiTooling)
  implementation(libs.androidx.hilt.dagger)
  implementation(libs.bundles.androidxLifecycle)
  implementation(libs.kotlin.coroutines.android)
  implementation(libs.thirdparty.accompanist.coil)
  implementation(libs.thirdparty.accompanist.flow)
  implementation(libs.thirdparty.moshi.lib)
  implementation(libs.thirdparty.moshix.metadatareflect)
  implementation(libs.thirdparty.retrofit.moshiConverter)
  testImplementation(libs.testing.kotlintest.junit)
  androidTestImplementation(libs.testing.kotlintest.junit)
}
