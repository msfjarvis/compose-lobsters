import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
  id("com.github.ben-manes.versions")
  id("nl.littlerobots.version-catalog-update")
}

versionCatalogUpdate {
  keep {
    // This clears out build-logic specific dependencies
    keepUnusedLibraries.set(true)
  }
}

fun isNonStable(version: String): Boolean {
  val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
  val regex = "^[0-9,.v-]+(-r)?$".toRegex()
  val isStable = stableKeyword || regex.matches(version)
  return isStable.not()
}

tasks.withType<DependencyUpdatesTask>().configureEach {
  rejectVersionIf {
    when (candidate.group) {
      "com.android.application",
      "com.android.library" -> true
      else -> isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
  }
  checkForGradleUpdate = false
  checkBuildEnvironmentConstraints = true
  outputFormatter = "json"
  outputDir = "build/dependencyUpdates"
  reportfileName = "report"
}
