plugins {
  id("com.android.library")
  kotlin("android")
  kotlin("plugin.serialization") version "1.4.21"
  `lobsters-plugin`
}

dependencies {
  implementation(Dependencies.Kotlin.Serialization.json)
}
