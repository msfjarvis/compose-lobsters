plugins {
  id("com.android.library")
  kotlin("android")
  kotlin("kapt")
  `lobsters-plugin`
}

dependencies {
  kapt(Dependencies.AndroidX.Hilt.daggerCompiler)
  api(Dependencies.ThirdParty.Retrofit.lib)
  implementation(project(":database"))
  implementation(Dependencies.AndroidX.Hilt.dagger)
  implementation(Dependencies.ThirdParty.Moshi.kotlinReflect)
  implementation(Dependencies.ThirdParty.Moshi.moshiMetadataReflect)
  implementation(Dependencies.ThirdParty.Retrofit.moshi)
  testImplementation(Dependencies.Kotlin.Coroutines.core)
  testImplementation(Dependencies.Testing.junit)
  testImplementation(Dependencies.Testing.mockWebServer)
}
