/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android

import android.content.Intent
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.v2.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test

class ReminderToggleTest {
  @get:Rule val compose = createEmptyComposeRule()

  @Test
  fun reminderSwitchUpdatesWithoutLeavingSettings() {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    ActivityScenario.launch<MainActivity>(Intent(context, MainActivity::class.java)).use {
      compose.onNodeWithContentDescription("Settings").performClick()
      val toggle = compose.onAllNodes(isToggleable())[0]
      toggle.assertIsOff()
      toggle.performClick()
      toggle.assertIsOn()
      toggle.performClick()
      toggle.assertIsOff()
    }
  }
}
