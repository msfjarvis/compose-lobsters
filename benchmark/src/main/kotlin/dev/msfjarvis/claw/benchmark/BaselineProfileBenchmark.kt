package dev.msfjarvis.claw.benchmark

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import org.junit.Rule
import org.junit.Test

class BaselineProfileBenchmark {
  @get:Rule val benchmarkRule = MacrobenchmarkRule()

  @Test
  fun startupNoCompilation() {
    startup(CompilationMode.None())
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
      pressHome()
      startActivityAndWait()
    }
  }
}
