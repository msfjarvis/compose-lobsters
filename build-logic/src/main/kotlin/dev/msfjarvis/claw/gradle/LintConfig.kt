/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import com.android.build.api.dsl.Lint
import org.gradle.api.Project

object LintConfig {
  fun Lint.configureLint(project: Project) {
    quiet = project.providers.environmentVariable("CI").isPresent
    abortOnError = true
    checkReleaseBuilds = true
    warningsAsErrors = true
    ignoreWarnings = false
    checkAllWarnings = true
    noLines = false
    showAll = true
    explainIssues = true
    textReport = false
    xmlReport = false
    htmlReport = true
    sarifReport = true
    enable += "ComposeM2Api"
    error += "ComposeM2Api"
    baseline = project.file("lint-baseline.xml")
  }
}
