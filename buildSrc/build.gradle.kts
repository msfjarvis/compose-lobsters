plugins { `kotlin-dsl` }

repositories {
  google()
  gradlePluginPortal()
  maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
  maven(url = "https://oss.sonatype.org/content/repositories/snapshots/") {
    content { includeGroup("com.google.dagger") }
  }
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
  implementation("com.android.tools.build:gradle:7.1.0-alpha01")
  // https://github.com/google/dagger/issues/2634
  implementation("com.google.dagger:hilt-android-gradle-plugin:HEAD-SNAPSHOT")
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.0")
  implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:1.5.0-1.0.0-alpha10")
  implementation("com.github.zafarkhaja:java-semver:0.9.0")
  implementation("com.diffplug.spotless:spotless-plugin-gradle:5.12.4")
  implementation("com.squareup.sqldelight:gradle-plugin:1.5.0")
  implementation("org.jetbrains.compose:compose-gradle-plugin:0.4.0-build209")
}
