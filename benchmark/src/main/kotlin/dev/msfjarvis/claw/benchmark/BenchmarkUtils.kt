/*
 * Copyright © 2022-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.benchmark

import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until

const val PACKAGE_NAME = "dev.msfjarvis.claw.android"
private const val AWAIT_TIMEOUT = 10_000L
private const val SAVE_BUTTON_ID = "save_button"
private const val NAV_ID_HOTTEST = "HOTTEST"
private const val NAV_ID_NEWEST = "NEWEST"
private const val NAV_ID_SAVED = "SAVED"

fun UiDevice.savePosts() {
  waitForObject(By.res(SAVE_BUTTON_ID)).click()
}

fun UiDevice.exploreScreens() {
  listOf(NAV_ID_HOTTEST, NAV_ID_NEWEST, NAV_ID_SAVED).forEach { tag ->
    waitForObject(By.res(tag)).click()
    waitForIdle()
  }
}

fun UiDevice.returnToHottestScreen() {
  waitForObject(By.res(NAV_ID_HOTTEST)).click()
}

private fun UiDevice.waitForObject(selector: BySelector, timeout: Long = AWAIT_TIMEOUT): UiObject2 {
  if (wait(Until.hasObject(selector), timeout)) {
    return findObject(selector)
  }

  error("Object with selector [$selector] not found")
}
