/*
 * Copyright © 2022-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle.signing

import com.android.build.api.dsl.ApplicationBuildType
import com.android.build.api.dsl.CommonExtension
import java.util.Properties
import org.gradle.api.Project

private const val KEYSTORE_CONFIG_PATH = "keystore.properties"

/** Configure signing for all build types. */
@Suppress("UnstableApiUsage")
internal fun Project.configureBuildSigning() {
  val keystoreConfigFile = isolated.rootProject.projectDirectory.file(KEYSTORE_CONFIG_PATH)
  if (keystoreConfigFile.asFile.exists()) {
    extensions.configure<CommonExtension<*, ApplicationBuildType, *, *, *, *>>("android") {
      val contents = providers.fileContents(keystoreConfigFile).asText
      val keystoreProperties = Properties()
      keystoreProperties.load(contents.get().byteInputStream())
      val releaseSigningConfig =
        signingConfigs.register("release") {
          keyAlias = keystoreProperties["keyAlias"] as String
          keyPassword = keystoreProperties["keyPassword"] as String
          storeFile =
            isolated.rootProject.projectDirectory
              .file(keystoreProperties["storeFile"] as String)
              .asFile
          storePassword = keystoreProperties["storePassword"] as String
        }
      buildTypes.configureEach { signingConfig = releaseSigningConfig.get() }
    }
  }
}
