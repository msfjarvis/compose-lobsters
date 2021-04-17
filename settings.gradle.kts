rootProject.name = "Claw"

include(":api")
include(":app")
include(":common")
include(":database")
include(":desktop")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {
  repositories {
    google()
    gradlePluginPortal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  }
}
