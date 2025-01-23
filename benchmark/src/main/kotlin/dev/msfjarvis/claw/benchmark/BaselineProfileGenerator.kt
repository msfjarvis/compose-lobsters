/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.benchmark

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BaselineProfileGenerator {
  @get:Rule val baselineProfileRule = BaselineProfileRule()
  private lateinit var device: UiDevice

  @Before
  fun setUp() {
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    device = UiDevice.getInstance(instrumentation)
  }

  @Test
  fun generateBaselineProfile() =
    baselineProfileRule.collect(
      packageName = PACKAGE_NAME,
      maxIterations = 8,
      includeInStartupProfile = true,
    ) {
      device.executeShellCommand("pm clear $PACKAGE_NAME")

      startActivityAndWait()

      device.waitForIdle()

      device.savePosts()

      device.exploreScreens()

      device.returnToHottestScreen()
    }
}
