plugins {
  id("com.android.library")
  kotlin("android")
  kotlin("kapt")
  kotlin("plugin.serialization") version "1.4.10"
  `lobsters-plugin`
}

dependencies {
  kapt(Dependencies.ThirdParty.Moshi.codegen)
  implementation(Dependencies.ThirdParty.Moshi.lib)
  implementation(Dependencies.Kotlin.Serialization.json)
}
