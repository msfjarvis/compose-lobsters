rootProject.name = "Claw"
include(":app", ":api", ":database")
enableFeaturePreview("GRADLE_METADATA")
pluginManagement {
  repositories {
    google()
    gradlePluginPortal()
  }
}
