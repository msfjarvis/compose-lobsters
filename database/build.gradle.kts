plugins {
  id("com.android.library")
  kotlin("android")
  kotlin("kapt")
  id("com.squareup.sqldelight")
  `lobsters-plugin`
}

dependencies {
  kapt(Dependencies.AndroidX.Hilt.daggerCompiler)
  implementation(Dependencies.AndroidX.Hilt.dagger)
  implementation(Dependencies.ThirdParty.Moshi.lib)
  implementation(Dependencies.ThirdParty.Moshi.moshiMetadataReflect)
  implementation(Dependencies.ThirdParty.SQLDelight.androidDriver)
  testImplementation(Dependencies.Kotlin.Coroutines.core)
  testImplementation(Dependencies.ThirdParty.SQLDelight.jvmDriver)
  testImplementation(Dependencies.Testing.junit)
}
