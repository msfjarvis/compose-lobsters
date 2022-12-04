/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle.signing

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import java.util.Properties
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

private const val KEYSTORE_CONFIG_PATH = "keystore.properties"

/** Configure signing for all build types. */
@Suppress("UnstableApiUsage")
internal fun Project.configureBuildSigning() {
  val keystoreConfigFile = rootProject.layout.projectDirectory.file(KEYSTORE_CONFIG_PATH)
  if (keystoreConfigFile.asFile.exists()) {
    extensions.configure<BaseAppModuleExtension> {
      val contents = providers.fileContents(keystoreConfigFile).asText
      val keystoreProperties = Properties()
      keystoreProperties.load(contents.get().byteInputStream())
      signingConfigs {
        register("release") {
          keyAlias = keystoreProperties["keyAlias"] as String
          keyPassword = keystoreProperties["keyPassword"] as String
          storeFile = rootProject.file(keystoreProperties["storeFile"] as String)
          storePassword = keystoreProperties["storePassword"] as String
        }
      }
      val signingConfig = signingConfigs.getByName("release")
      buildTypes.all { setSigningConfig(signingConfig) }
    }
  }
}
