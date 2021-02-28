plugins {
  kotlin("jvm")
  `lobsters-plugin`
}

dependencies {
  api(Dependencies.ThirdParty.Retrofit.lib)
  implementation(Dependencies.ThirdParty.Moshi.moshiMetadataReflect)
  implementation(Dependencies.ThirdParty.Retrofit.moshi)
  testImplementation(Dependencies.Kotlin.Coroutines.core)
  testImplementation(Dependencies.Testing.junit)
  testImplementation(Dependencies.Testing.mockWebServer)
}
