plugins {
  id("com.android.library")
  kotlin("android")
  kotlin("kapt")
  id("com.squareup.sqldelight")
  `lobsters-plugin`
}

dependencies {
  kapt(Dependencies.ThirdParty.Moshi.codegen)
  implementation(Dependencies.ThirdParty.Moshi.lib)
  testImplementation(Dependencies.Kotlin.Coroutines.core)
  testImplementation(Dependencies.ThirdParty.SQLDelight.jvmDriver)
  testImplementation(Dependencies.Testing.junit)
}
