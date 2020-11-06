plugins {
  id("com.android.library")
  kotlin("android")
  kotlin("kapt")
  `lobsters-plugin`
}

dependencies {
  kapt(Dependencies.ThirdParty.Moshi.codegen)
  implementation(Dependencies.ThirdParty.Moshi.lib)
}
