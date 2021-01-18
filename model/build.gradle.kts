plugins {
  id("com.android.library")
  kotlin("android")
  kotlin("kapt")
  `lobsters-plugin`
}

dependencies {
  kapt(Dependencies.AndroidX.Hilt.daggerCompiler)
  kapt(Dependencies.ThirdParty.Moshi.codegen)
  api(Dependencies.ThirdParty.Retrofit.lib)
  implementation(Dependencies.AndroidX.Hilt.dagger)
  implementation(Dependencies.ThirdParty.Moshi.lib)
  implementation(Dependencies.ThirdParty.Retrofit.moshi)
  testImplementation(Dependencies.Kotlin.Coroutines.core)
  testImplementation(Dependencies.Testing.junit)
  testImplementation(Dependencies.Testing.mockWebServer)
}
