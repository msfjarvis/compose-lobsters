/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import com.android.build.api.dsl.Lint
import org.gradle.api.Project

object LintConfig {
  fun Lint.configureLint(project: Project, isJVM: Boolean = false) {
    quiet = project.providers.environmentVariable("CI").isPresent
    abortOnError = true
    checkDependencies = true
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
    sarifReport = false
    if (!isJVM) {
      enable += "ComposeM2Api"
      error += "ComposeM2Api"
      // The Lint baseline message changes too frequently for this
      disable += "Aligned16KB"
    }
    baseline = project.file("lint-baseline.xml")
    // This is extremely annoying
    disable += "AndroidGradlePluginVersion"
    disable += "GradleDependency"
    disable += "NewerVersionAvailable"
    // Can't do anything about this
    disable += "ObsoleteLintCustomCheck"
    // I trust myself enough to not deal with the constantly changing error message.
    disable += "InvalidPackage"
  }
}
