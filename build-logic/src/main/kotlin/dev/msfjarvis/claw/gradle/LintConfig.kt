/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import com.android.build.api.dsl.Lint
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register

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
    sarifReport = true
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
  }

  fun configureRootProject(project: Project) {
    project.tasks.register<Copy>("collectLintReports") {
      into(project.layout.buildDirectory.dir("lint-reports"))
    }
  }

  fun configureSubProject(project: Project) {
    val collectorTask = project.rootProject.tasks.named<Copy>("collectLintReports")
    val lintTask = project.tasks.named("lint")
    val name = project.name

    collectorTask.configure {
      from(project.layout.buildDirectory.file("reports")) {
        include("*.sarif")
        rename { it.replace("-results", "-results-$name") }
      }
      dependsOn(lintTask)
    }
  }
}
