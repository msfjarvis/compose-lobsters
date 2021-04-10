rootProject.name = "Claw"

include(":api")
include(":app")
include(":common")
include(":database")
include(":desktop")

pluginManagement {
  repositories {
    google()
    gradlePluginPortal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  }
}
