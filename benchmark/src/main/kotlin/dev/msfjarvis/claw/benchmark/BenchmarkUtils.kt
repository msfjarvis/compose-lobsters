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

const val PACKAGE_NAME = "dev.msfjarvis.claw.android"

fun MacrobenchmarkScope.exploreUI(device: UiDevice) {
  startActivityAndWait()
  device.run {
    listOf("HOTTEST", "NEWEST", "SAVED").forEach { desc ->
      findObject(By.desc(desc)).click()
      waitForIdle()
    }
    findObject(By.desc("HOTTEST")).click()
    waitForIdle()
    findObjects(By.desc("Open comments")).first().click()
    waitForIdle()
  }
}
