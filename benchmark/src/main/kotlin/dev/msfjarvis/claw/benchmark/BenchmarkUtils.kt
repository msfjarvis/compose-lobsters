/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.benchmark

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until

const val PACKAGE_NAME = "dev.msfjarvis.claw.android"
private const val AWAIT_TIMEOUT = 10_000L
private const val SAVE_BUTTON_DESC = "Add to saved posts"
private const val NAV_ID_HOTTEST = "HOTTEST"
private const val NAV_ID_NEWEST = "NEWEST"
private const val NAV_ID_SAVED = "SAVED"
private const val COMMENT_BUTTON_DESC = "Open comments"

fun MacrobenchmarkScope.exploreUI(device: UiDevice) {
  startActivityAndWait()
  device.run {
    savePosts()

    exploreScreens()

    returnToHottestScreen()

    openCommentsScreen()
  }
}

private fun UiDevice.waitForSubmitterName() {
  wait(Until.hasObject(By.textContains("Submitted by")), AWAIT_TIMEOUT)
}

private fun UiDevice.savePosts() {
  waitForSubmitterName()
  findObjects(By.desc(SAVE_BUTTON_DESC)).forEach { btn -> btn.click() }
}

private fun UiDevice.exploreScreens() {
  listOf(NAV_ID_HOTTEST, NAV_ID_NEWEST, NAV_ID_SAVED).forEach { tag ->
    findObject(By.res(tag)).click()
    waitForSubmitterName()
  }
}

private fun UiDevice.returnToHottestScreen() {
  findObject(By.res(NAV_ID_HOTTEST)).click()
  waitForSubmitterName()
}

private fun UiDevice.openCommentsScreen() {
  findObjects(By.desc(COMMENT_BUTTON_DESC)).first().click()
  waitForSubmitterName()
}
