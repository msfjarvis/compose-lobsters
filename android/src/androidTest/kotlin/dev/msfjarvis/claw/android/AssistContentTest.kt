/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android

import android.app.assist.AssistContent
import android.content.Intent
import androidx.core.net.toUri
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test

class AssistContentTest {
  @Test
  fun assistUriFollowsCommentsAndBackNavigation() {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    fun deepLink(destination: String) =
      Intent(context, MainActivity::class.java).apply {
        data = "${BuildConfig.DEEPLINK_SCHEME}://$destination".toUri()
      }

    ActivityScenario.launch<MainActivity>(deepLink("comments/ktew3s")).use { scenario ->
      scenario.assertAssistUri("https://lobste.rs/s/ktew3s/c")
      scenario.onActivity {
        InstrumentationRegistry.getInstrumentation()
          .callActivityOnNewIntent(it, deepLink("comments/d7m5j1"))
      }
      scenario.assertAssistUri("https://lobste.rs/s/d7m5j1/c")
      scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
      scenario.assertAssistUri("https://lobste.rs/s/ktew3s/c")
      scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
      scenario.assertAssistUri("https://lobste.rs/")
      scenario.onActivity {
        InstrumentationRegistry.getInstrumentation().callActivityOnNewIntent(it, deepLink("newest"))
      }
      scenario.assertAssistUri("https://lobste.rs/newest")
      scenario.onActivity {
        InstrumentationRegistry.getInstrumentation().callActivityOnNewIntent(it, deepLink("saved"))
      }
      scenario.assertAssistUri(null)
    }
  }

  private fun ActivityScenario<MainActivity>.assertAssistUri(expected: String?) {
    var actual: String? = null
    repeat(50) {
      InstrumentationRegistry.getInstrumentation().waitForIdleSync()
      onActivity { activity ->
        actual = AssistContent().also(activity::onProvideAssistContent).webUri?.toString()
      }
      if (actual == expected) return
      Thread.sleep(100)
    }
    assertEquals(expected, actual)
  }
}
