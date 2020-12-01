plugins {
  id("com.android.library")
  kotlin("android")
  kotlin("kapt")
  kotlin("plugin.serialization") version "1.4.21"
  `lobsters-plugin`
}

dependencies {
  kapt(Dependencies.AndroidX.Hilt.daggerCompiler)
  kapt(Dependencies.AndroidX.Hilt.daggerHiltCompiler)
  api(Dependencies.Kotlin.Ktor.clientCore)
  implementation(Dependencies.AndroidX.Hilt.dagger)
  implementation(Dependencies.Kotlin.Serialization.json)
  implementation(Dependencies.Kotlin.Ktor.clientJson)
  implementation(Dependencies.Kotlin.Ktor.clientOkHttp)
  implementation(Dependencies.Kotlin.Ktor.clientSerialization)
  testImplementation(Dependencies.Testing.junit)
  testImplementation(Dependencies.Kotlin.Ktor.clientTest)
}
