/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle.versioning

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.VariantOutputConfiguration
import com.github.zafarkhaja.semver.Version
import java.util.Properties
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register

/**
 * A Gradle [Plugin] that takes a [Project] with the `com.android.application` applied and
 * dynamically sets the versionCode and versionName properties based on values read from a
 * [VERSIONING_PROP_FILE] file in the [Project.getProjectDir] directory. It also adds Gradle tasks
 * to bump the major, minor, and patch versions along with one to prepare the next snapshot.
 */
@Suppress("Unused")
class VersioningPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    with(project) {
      pluginManager.apply("com.android.application")
      val propFile = layout.projectDirectory.file(VERSIONING_PROP_FILE)
      require(propFile.asFile.exists()) {
        "A 'version.properties' file must exist in the project subdirectory to use this plugin"
      }
      val contents = providers.fileContents(propFile).asText
      val versionProps = contents.map { text -> Properties().also { it.load(text.reader()) } }
      val versionName =
        providers
          .gradleProperty("VERSION_NAME")
          .orElse(
            versionProps.map { props ->
              requireNotNull(props.getProperty(VERSIONING_PROP_VERSION_NAME)) {
                "version.properties must contain a '$VERSIONING_PROP_VERSION_NAME' property"
              }
            }
          )
      val versionCode =
        providers
          .gradleProperty("VERSION_CODE")
          .orElse(
            versionProps.map { props ->
              requireNotNull(props.getProperty(VERSIONING_PROP_VERSION_CODE)) {
                "version.properties must contain a '$VERSIONING_PROP_VERSION_CODE' property"
              }
            }
          )
          .map(String::toInt)

      project.pluginManager.withPlugin("com.android.application") {
        extensions.configure<ApplicationAndroidComponentsExtension> {
          onVariants { variant ->
            val mainOutput =
              variant.outputs.single {
                it.outputType == VariantOutputConfiguration.OutputType.SINGLE
              }
            mainOutput.versionName.set(versionName)
            mainOutput.versionCode.set(versionCode)
          }
        }
      }
      val version = versionName.map(Version::parse)
      tasks.register<VersioningTask>("clearPreRelease") {
        description = "Remove the pre-release suffix from the version"
        semverString.set(version.map(Version::toStableVersion).map(Version::toString))
        propertyFile.set(propFile)
      }
      tasks.register<VersioningTask>("bumpMajor") {
        description = "Increment the major version"
        semverString.set(version.map(Version::nextMajorVersion).map(Version::toString))
        propertyFile.set(propFile)
      }
      tasks.register<VersioningTask>("bumpMinor") {
        description = "Increment the minor version"
        semverString.set(version.map(Version::nextMinorVersion).map(Version::toString))
        propertyFile.set(propFile)
      }
      tasks.register<VersioningTask>("bumpPatch") {
        description = "Increment the patch version"
        semverString.set(version.map(Version::nextPatchVersion).map(Version::toString))
        propertyFile.set(propFile)
      }
      tasks.register<VersioningTask>("bumpSnapshot") {
        description = "Increment the minor version and add the `SNAPSHOT` suffix"
        semverString.set(version.map { it.nextMinorVersion("SNAPSHOT") }.map(Version::toString))
        propertyFile.set(propFile)
      }
    }
  }
}
