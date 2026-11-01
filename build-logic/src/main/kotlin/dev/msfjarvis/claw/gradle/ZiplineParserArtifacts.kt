/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import org.gradle.api.attributes.Attribute

object ZiplineParserArtifacts {
  val formatAttribute: Attribute<String> =
    Attribute.of("dev.msfjarvis.claw.zipline-parser-format", String::class.java)
  const val PRODUCTION = "production"
}
