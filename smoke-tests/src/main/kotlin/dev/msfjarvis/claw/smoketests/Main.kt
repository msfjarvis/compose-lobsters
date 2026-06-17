/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.smoketests

import kotlinx.coroutines.runBlocking

fun main() {
  val graph = createSmokeTestsGraph()
  val result = runBlocking { graph.smokeProbeRunner.run() }
  if (!result.isSuccess) {
    System.err.println("Smoke probe failed")
    result.errors().forEach { System.err.println(it) }
    kotlin.system.exitProcess(1)
  }
  println("Smoke probe passed")
}
