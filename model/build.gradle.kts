plugins {
  id("com.android.library")
  kotlin("android")
  kotlin("plugin.serialization") version "1.4.10"
  `lobsters-plugin`
}

dependencies {
  implementation(Dependencies.Kotlin.Serialization.json)
  api(Dependencies.ThirdParty.Kodein.apiJvm)
}
