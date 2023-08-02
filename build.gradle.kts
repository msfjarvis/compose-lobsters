/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
plugins {
  id("dev.msfjarvis.claw.spotless")
  id("dev.msfjarvis.claw.versions")
  id("dev.msfjarvis.claw.kotlin-common")
  alias(libs.plugins.modulegraph)
}

moduleGraphConfig {
  readmePath.set(project.layout.projectDirectory.file("README.md").asFile.absolutePath)
  heading.set("## Dependency Diagram")
}
