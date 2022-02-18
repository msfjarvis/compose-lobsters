pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") {
      name = "JetBrains Compose Dev Repository"
      content { includeGroupByRegex("org\\.jetbrains\\.compose.*") }
    }
    google()
  }
  plugins { id("org.jetbrains.compose") version "1.1.0-alpha05" apply false }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") {
      name = "JetBrains Compose Dev Repository"
      content { includeGroupByRegex("org\\.jetbrains\\.compose.*") }
    }
    google()
  }
}

rootProject.name = "Claw"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":android")

include(":api")

include(":common")

include(":database")

include(":desktop")

include(":model")
