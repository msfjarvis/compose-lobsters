plugins {
  kotlin("jvm")
  id("com.squareup.sqldelight")
  `lobsters-plugin`
}

dependencies {
  testImplementation(Dependencies.Kotlin.Coroutines.core)
  testImplementation(Dependencies.ThirdParty.SQLDelight.jvmDriver)
  testImplementation(kotlin("test-junit"))
}
