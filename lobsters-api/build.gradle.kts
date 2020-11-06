plugins {
  id("com.android.library")
  kotlin("android")
  kotlin("kapt")
  `lobsters-plugin`
}

dependencies {
  implementation(project(":model"))
  implementation(Dependencies.ThirdParty.Retrofit.lib)
  implementation(Dependencies.ThirdParty.Retrofit.moshi)
  kaptTest(Dependencies.ThirdParty.Moshi.codegen)
  testImplementation(Dependencies.Testing.junit)
  testImplementation(Dependencies.Kotlin.Coroutines.core)
  testImplementation(Dependencies.Testing.mockWebServer)
}
