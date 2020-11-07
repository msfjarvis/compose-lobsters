plugins {
  id("com.android.library")
  kotlin("android")
  kotlin("kapt")
  kotlin("plugin.serialization") version "1.4.10"
  `lobsters-plugin`
}

dependencies {
  implementation(project(":model"))
  implementation(Dependencies.ThirdParty.Retrofit.lib)
  implementation(Dependencies.ThirdParty.Retrofit.moshi)
  implementation(Dependencies.Kotlin.Serialization.json)
  implementation(Dependencies.ThirdParty.retrofitSerialization)
  kaptTest(Dependencies.ThirdParty.Moshi.codegen)
  testImplementation(Dependencies.Testing.junit)
  testImplementation(Dependencies.Kotlin.Coroutines.core)
  testImplementation(Dependencies.Testing.mockWebServer)
}
