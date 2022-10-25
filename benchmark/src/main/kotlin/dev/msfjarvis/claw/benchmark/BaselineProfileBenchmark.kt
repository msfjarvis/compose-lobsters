/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.benchmark

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@LargeTest
class BaselineProfileBenchmark {
  @get:Rule val benchmarkRule = MacrobenchmarkRule()
  private lateinit var device: UiDevice

  @Before
  fun setUp() {
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    device = UiDevice.getInstance(instrumentation)
  }

  @Test
  fun startupNoCompilation() {
    startup(CompilationMode.None())
  }

  @Test
  fun startupBaselineFullyCompiled() {
    startup(CompilationMode.Full())
  }

  @Test
  fun startupBaselineProfile() {
    startup(CompilationMode.Partial(baselineProfileMode = BaselineProfileMode.Require))
  }

  private fun startup(compilationMode: CompilationMode) {
    benchmarkRule.measureRepeated(
      packageName = PACKAGE_NAME,
      metrics = listOf(StartupTimingMetric()),
      iterations = 10,
      startupMode = StartupMode.COLD,
      compilationMode = compilationMode
    ) {
      exploreUI(device)
    }
  }
}
