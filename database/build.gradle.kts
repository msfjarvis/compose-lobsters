plugins {
  id("com.android.library")
  kotlin("android")
  id("com.squareup.sqldelight")
  `lobsters-plugin`
}

dependencies {
  implementation(project(":model"))
}
