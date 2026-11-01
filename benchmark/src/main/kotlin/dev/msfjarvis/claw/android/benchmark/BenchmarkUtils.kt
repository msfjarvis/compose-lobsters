/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.benchmark

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until

const val PACKAGE_NAME = "dev.msfjarvis.claw.android"
private const val AWAIT_TIMEOUT = 10_000L
private const val SAVE_BUTTON_ID = "save_button"
private const val COMMENTS_BUTTON_ID = "comments_button"
private const val COMMENTS_HEADER_ID = "comments_header"
private const val NAV_ID_HOTTEST = "HOTTEST"
private const val NAV_ID_NEWEST = "NEWEST"
private const val NAV_ID_SAVED = "SAVED"

fun MacrobenchmarkScope.exploreUI(warmupStep: MacrobenchmarkScope.() -> Unit = {}) {
  warmupStep()

  startActivityAndWait()

  device.savePosts()

  device.viewComments()

  device.exploreScreens()

  device.returnToHottestScreen()
}

private fun UiDevice.savePosts() {
  waitForIdle()
  waitForObject(By.res(SAVE_BUTTON_ID)).click()
}

private fun UiDevice.viewComments() {
  waitForIdle()
  waitForObject(By.res(COMMENTS_BUTTON_ID)).click()
  waitForObject(By.res(COMMENTS_HEADER_ID))
  pressBack()
}

private fun UiDevice.exploreScreens() {
  waitForIdle()
  listOf(NAV_ID_HOTTEST, NAV_ID_NEWEST, NAV_ID_SAVED).forEach { tag ->
    waitForObject(By.res(tag)).click()
    waitForIdle()
  }
}

private fun UiDevice.returnToHottestScreen() {
  waitForIdle()
  waitForObject(By.res(NAV_ID_HOTTEST)).click()
}

private fun UiDevice.waitForObject(selector: BySelector, timeout: Long = AWAIT_TIMEOUT): UiObject2 {
  if (wait(Until.hasObject(selector), timeout)) {
    return findObject(selector)
  }

  error("Object with selector [$selector] not found")
}
