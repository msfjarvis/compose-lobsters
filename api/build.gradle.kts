@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

plugins { alias(libs.plugins.kotlin.jvm) }

dependencies {
  api(projects.model)
  api(libs.retrofit.lib)
  implementation(libs.kotlinx.serialization.core)
  testImplementation(libs.kotlinx.serialization.json)
  testImplementation(libs.kotlin.coroutines.core)
  testImplementation(kotlin("test-junit"))
  testImplementation(libs.retrofit.kotlinxSerializationConverter) { isTransitive = false }
  testImplementation(libs.testing.mockWebServer)
}
