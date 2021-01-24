plugins {
  id("com.android.library")
  kotlin("android")
  id("com.squareup.sqldelight")
  `lobsters-plugin`
}

dependencies {
  implementation(Dependencies.ThirdParty.Moshi.lib)
  implementation(Dependencies.ThirdParty.Moshi.kotlinReflect)
  testImplementation(Dependencies.Kotlin.Coroutines.core)
  testImplementation(Dependencies.ThirdParty.SQLDelight.jvmDriver)
  testImplementation(Dependencies.Testing.junit)
}
