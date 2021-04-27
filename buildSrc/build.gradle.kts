plugins { `kotlin-dsl` }

repositories {
  google()
  gradlePluginPortal()
  maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

gradlePlugin {
  plugins {
    register("lobsters") {
      id = "lobsters-plugin"
      implementationClass = "LobstersPlugin"
    }
    register("coreLibraryDesugaring") {
      id = "core-library-desugaring"
      implementationClass = "CoreLibraryDesugaringPlugin"
    }
    register("versioning") {
      id = "versioning-plugin"
      implementationClass = "VersioningPlugin"
    }
  }
}

dependencies {
  implementation("com.android.tools.build:gradle:7.0.0-alpha14")
  implementation("com.google.dagger:hilt-android-gradle-plugin:2.35")
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.32")
  implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:1.4.32-1.0.0-alpha08")
  implementation("com.github.zafarkhaja:java-semver:0.9.0")
  implementation("com.karumi:shot:5.10.3")
  implementation("com.diffplug.spotless:spotless-plugin-gradle:5.12.4")
  implementation("com.squareup.sqldelight:gradle-plugin:1.5.0")
  implementation("org.jetbrains.compose:compose-gradle-plugin:0.4.0-build185")
}
