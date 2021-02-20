plugins {
  id("com.android.library")
  kotlin("android")
  id("com.squareup.sqldelight")
  `lobsters-plugin`
}

dependencies {
  implementation(Dependencies.ThirdParty.Moshi.lib)
  implementation(Dependencies.ThirdParty.Moshi.moshiMetadataReflect)
  testImplementation(Dependencies.Kotlin.Coroutines.core)
  testImplementation(Dependencies.ThirdParty.SQLDelight.jvmDriver)
  testImplementation(Dependencies.Testing.junit)
}
