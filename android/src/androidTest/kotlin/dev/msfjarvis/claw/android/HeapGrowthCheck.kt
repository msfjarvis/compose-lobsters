/*
 * Copyright © 2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import com.google.common.truth.Truth.assertThat
import de.mannodermaus.junit5.ActivityScenarioExtension
import leakcanary.repeatingAndroidInProcessScenario
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import shark.ObjectGrowthDetector
import shark.forAndroidHeap

class HeapGrowthCheck {
  @JvmField
  @RegisterExtension
  val scenarioExtension = ActivityScenarioExtension.launch<MainActivity>()
  private val detector = ObjectGrowthDetector.forAndroidHeap().repeatingAndroidInProcessScenario()
  private lateinit var device: UiDevice

  @BeforeEach
  fun setUp() {
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    device = UiDevice.getInstance(instrumentation)
  }

  @Test
  fun verify_heap_growth() {
    val heapGrowth = detector.findRepeatedlyGrowingObjects { device.exploreScreens() }
    assertThat(heapGrowth.growingObjects).isEmpty()
  }

  private companion object {
    fun UiDevice.exploreScreens() {
      listOf("HOTTEST", "NEWEST", "SAVED").forEach { tag ->
        waitForObject(By.res(tag)).click()
        waitForIdle()
      }
    }

    private fun UiDevice.waitForObject(selector: BySelector, timeout: Long = 10_000L): UiObject2 {
      if (wait(Until.hasObject(selector), timeout)) {
        return findObject(selector)
      }

      error("Object with selector [$selector] not found")
    }
  }
}
