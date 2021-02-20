plugins {
  id("com.android.library")
  kotlin("android")
  `lobsters-plugin`
}

dependencies {
  api(Dependencies.ThirdParty.Retrofit.lib)
  implementation(project(":database"))
  implementation(Dependencies.ThirdParty.Moshi.moshiMetadataReflect)
  implementation(Dependencies.ThirdParty.Retrofit.moshi)
  testImplementation(Dependencies.Kotlin.Coroutines.core)
  testImplementation(Dependencies.Testing.junit)
  testImplementation(Dependencies.Testing.mockWebServer)
}
