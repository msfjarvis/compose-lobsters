plugins {
  id("com.android.library")
  kotlin("android")
  kotlin("plugin.serialization") version "1.4.20"
  `lobsters-plugin`
}

dependencies {
  implementation(Dependencies.Kotlin.Serialization.json)
}
