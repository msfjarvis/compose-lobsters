/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.smoketests

import dev.msfjarvis.claw.api.LobstersApi
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.createGraphFactory

@DependencyGraph(AppScope::class)
interface SmokeTestsGraph {
  val lobstersApi: LobstersApi
  val smokeProbeRunner: SmokeProbeRunner

  @DependencyGraph.Factory
  fun interface Factory {
    fun create(): SmokeTestsGraph
  }
}

fun createSmokeTestsGraph(): SmokeTestsGraph =
  createGraphFactory<SmokeTestsGraph.Factory>().create()
