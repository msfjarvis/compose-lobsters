@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

plugins {
  kotlin("jvm")
  kotlin("plugin.serialization")
  id("dev.msfjarvis.claw.kotlin-common")
}

dependencies { implementation(libs.kotlinx.serialization.core) }
