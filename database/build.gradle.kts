plugins {
  id("com.android.library")
  kotlin("android")
  id("com.squareup.sqldelight")
  `lobsters-plugin`
}

dependencies {
  implementation(project(":model"))
  implementation(Dependencies.ThirdParty.Moshi.lib)
  testImplementation(Dependencies.Kotlin.Coroutines.core)
  testImplementation(Dependencies.ThirdParty.SQLDelight.jvmDriver)
  testImplementation(Dependencies.Testing.junit)
}
