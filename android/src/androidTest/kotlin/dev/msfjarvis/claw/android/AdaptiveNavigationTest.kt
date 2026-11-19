/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android

import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.core.net.toUri
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test

class AdaptiveNavigationTest {
  @get:Rule val compose = createEmptyComposeRule()

  @Test
  fun expandedCommentsKeepFeedNavigationWithoutGlobalBackArrow() {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    assumeTrue(context.resources.configuration.screenWidthDp >= 840)
    val intent =
      Intent(context, MainActivity::class.java).apply {
        data = "${BuildConfig.DEEPLINK_SCHEME}://comments/ktew3s".toUri()
      }
    ActivityScenario.launch<MainActivity>(intent).use { scenario ->
      compose.waitUntil(timeoutMillis = 10_000) {
        var uri: String? = null
        scenario.onActivity { uri = it.webUri }
        uri == "https://lobste.rs/s/ktew3s/c"
      }
      compose.onNodeWithTag("HOTTEST").assertIsDisplayed()
      compose.onNodeWithContentDescription("Go back to previous screen").assertDoesNotExist()
    }
  }
}
