pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
  }
  plugins { id("org.jetbrains.compose") version "1.1.0-alpha04" apply false }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
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
