@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  id("dev.msfjarvis.claw.spotless")
  id("dev.msfjarvis.claw.versions")
  id("dev.msfjarvis.claw.kotlin-common")
  alias(libs.plugins.hilt) apply false
}
