plugins {
  kotlin("jvm")
  id("dev.msfjarvis.claw.kotlin-library")
}

dependencies {
  api(libs.kotlinx.coroutines.core)
  implementation(libs.dagger.hilt.core)
}
