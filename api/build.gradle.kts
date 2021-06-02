import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
  id("com.google.devtools.ksp") version "1.5.10-1.0.0-beta01"
}

dependencies {
  api(libs.thirdparty.retrofit.lib)
  ksp(libs.thirdparty.moshix.ksp)
  implementation(libs.thirdparty.moshi.lib)
  implementation(libs.thirdparty.retrofit.moshiConverter) { exclude(group = "com.squareup.moshi") }
  testImplementation(libs.kotlin.coroutines.core)
  testImplementation(libs.testing.kotlintest.junit)
  testImplementation(libs.testing.mockWebServer)
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_11.toString()
    languageVersion = "1.5"
  }
}
