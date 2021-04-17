plugins {
  kotlin("jvm")
  id("com.google.devtools.ksp")
  `lobsters-plugin`
}

dependencies {
  api(libs.thirdparty.retrofit.lib)
  ksp(libs.thirdparty.moshi.ksp)
  implementation(libs.thirdparty.retrofit.moshiConverter)
  testImplementation(libs.kotlin.coroutines.core)
  testImplementation(kotlin("test-junit"))
  testImplementation(libs.testing.mockWebServer)
}
