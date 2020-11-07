plugins {
  id("com.android.library")
  kotlin("android")
  kotlin("plugin.serialization") version "1.4.10"
  `lobsters-plugin`
}

dependencies {
  implementation(project(":model"))
  implementation(Dependencies.Kotlin.Serialization.json)
  implementation(Dependencies.ThirdParty.retrofitSerialization)
  testImplementation(Dependencies.Testing.junit)
  testImplementation(Dependencies.Kotlin.Coroutines.core)
  testImplementation(Dependencies.Testing.mockWebServer)
}
