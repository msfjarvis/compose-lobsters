/*
 * Copyright © 2022-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.benchmark

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BaselineProfileBenchmark {
  @get:Rule val benchmarkRule = MacrobenchmarkRule()
  private lateinit var device: UiDevice

  @Before
  fun setUp() {
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    device = UiDevice.getInstance(instrumentation)
  }

  @Test
  fun noCompilation() {
    exploreUI(CompilationMode.None())
  }

  @Test
  fun fullyCompiled() {
    exploreUI(CompilationMode.Full())
  }

  @Test
  fun baselineProfile() {
    exploreUI(CompilationMode.Partial(baselineProfileMode = BaselineProfileMode.Require))
  }

  private fun exploreUI(compilationMode: CompilationMode) {
    benchmarkRule.measureRepeated(
      packageName = PACKAGE_NAME,
      metrics = listOf(FrameTimingMetric(), StartupTimingMetric()),
      compilationMode = compilationMode,
      startupMode = StartupMode.COLD,
      iterations = 10,
    ) {
      device.executeShellCommand("pm clear $PACKAGE_NAME")

      startActivityAndWait()

      device.waitForIdle()

      device.savePosts()

      device.exploreScreens()

      device.returnToHottestScreen()
    }
  }
}
