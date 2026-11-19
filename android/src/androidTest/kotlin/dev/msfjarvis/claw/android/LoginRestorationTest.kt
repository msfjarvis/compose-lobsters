/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import dev.msfjarvis.claw.android.ui.screens.LoginScreen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Rule
import org.junit.Test

class LoginRestorationTest {
  @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

  @Test
  @SuppressLint("DeprecatedCall") // Exercise the platform WebView history APIs used by the screen.
  fun restoringLoginRecreatesWebViewWithNavigationHistory() {
    val restoration = StateRestorationTester(compose)
    restoration.setContent { LoginScreen(onLoginSuccess = { _, _ -> }, popBackStack = {}) }
    fun webView(): WebView? = findWebView(compose.activity.window.decorView)
    fun onUi(condition: () -> Boolean): Boolean {
      var result = false
      compose.runOnIdle { result = condition() }
      return result
    }
    compose.waitUntil(10_000) { onUi { webView() != null } }
    lateinit var original: WebView
    compose.runOnIdle {
      original = checkNotNull(webView())
      original.stopLoading()
      original.loadUrl("about:blank#/login/restored")
    }
    compose.waitUntil(10_000) { onUi { webView()?.url == "about:blank#/login/restored" } }
    restoration.emulateSavedInstanceStateRestore()
    compose.waitUntil(10_000) { onUi { webView() != null && webView() !== original } }
    compose.runOnIdle {
      val restored = checkNotNull(webView())
      assertNotSame(original, restored)
      assertEquals("about:blank#/login/restored", restored.copyBackForwardList().currentItem?.url)
    }
  }

  private fun findWebView(view: View): WebView? {
    if (view is WebView) return view
    if (view is ViewGroup) {
      for (index in 0 until view.childCount) {
        findWebView(view.getChildAt(index))?.let {
          return it
        }
      }
    }
    return null
  }
}
