import com.diffplug.gradle.spotless.SpotlessExtension

internal fun SpotlessExtension.configure() {
  ratchetFrom = "origin/develop"
  kotlin {
    ktfmt().googleStyle()
    target("src/**/*.kt", "**/*.kts")
  }
  format("xml") {
    target("**/*.xml")
    trimTrailingWhitespace()
    indentWithSpaces()
    endWithNewline()
  }
}
