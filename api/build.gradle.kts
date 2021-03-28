plugins {
  kotlin("jvm")
  id("com.google.devtools.ksp") version "1.4.31-1.0.0-alpha06"
  `lobsters-plugin`
}

dependencies {
  api(Dependencies.ThirdParty.Retrofit.lib)
  ksp(Dependencies.ThirdParty.Moshi.ksp)
  implementation(Dependencies.ThirdParty.Retrofit.moshi)
  testImplementation(Dependencies.Kotlin.Coroutines.core)
  testImplementation(kotlin("test-junit"))
  testImplementation(Dependencies.Testing.mockWebServer)
}
