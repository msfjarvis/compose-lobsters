package dev.msfjarvis.claw.benchmark

import androidx.benchmark.macro.ExperimentalBaselineProfilesApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalBaselineProfilesApi
@MediumTest
class BaselineProfileGenerator {
  @get:Rule val baselineProfileRule = BaselineProfileRule()
  private lateinit var device: UiDevice

  @Before
  fun setUp() {
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    device = UiDevice.getInstance(instrumentation)
  }

  @Test
  fun startup() =
    baselineProfileRule.collectBaselineProfile(packageName = PACKAGE_NAME) { exploreUI(device) }
}
