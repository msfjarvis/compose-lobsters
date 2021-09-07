plugins {
  kotlin("jvm")
  id("com.google.devtools.ksp") version "1.5.30-1.0.0"
}

dependencies {
  api(libs.retrofit.lib)
  ksp(libs.moshix.ksp)
  implementation(libs.moshi.lib)
  implementation(libs.retrofit.moshiConverter) { exclude(group = "com.squareup.moshi") }
  testImplementation(libs.kotlin.coroutines.core)
  testImplementation(kotlin("test-junit"))
  testImplementation(libs.testing.mockWebServer)
}
