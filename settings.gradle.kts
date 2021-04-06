rootProject.name = "Claw"

include(":app", ":api", ":common", ":database", ":desktop")

enableFeaturePreview("GRADLE_METADATA")
pluginManagement {
  repositories {
    google()
    gradlePluginPortal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  }
}
