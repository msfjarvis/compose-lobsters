rootProject.name = "Claw"
include(":app", ":api", ":common", ":database", ":desktop")
pluginManagement {
  repositories {
    google()
    gradlePluginPortal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  }
}
