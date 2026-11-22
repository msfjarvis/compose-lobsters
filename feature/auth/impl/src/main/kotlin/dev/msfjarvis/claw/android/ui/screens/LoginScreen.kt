/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.msfjarvis.claw.core.network.CHROME_USER_AGENT

private const val LOGIN_URL = "https://lobste.rs/login"
private const val LOBSTERS_URL = "https://lobste.rs"

private class LoginWebViewState(var restoredState: Bundle? = null) {
  var webView: WebView? = null

  fun reset() {
    webView = null
    restoredState = null
  }

  companion object {
    val Saver =
      Saver<LoginWebViewState, Bundle>(
        save = { state ->
          // The platform API is still required to restore WebView navigation history.
          @SuppressLint("DeprecatedCall")
          state.webView?.let { view -> Bundle().also { view.saveState(it) } }
            ?: state.restoredState
            ?: Bundle()
        },
        restore = { LoginWebViewState(it) },
      )
  }
}

internal fun parseAuthenticatedUsername(usernameResult: String): String? {
  return usernameResult.removeSurrounding("\"").takeUnless { it.isBlank() || it == "null" }
}

@Composable
fun LoginScreen(
  onLoginSuccess: (String, String) -> Unit,
  popBackStack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var isLoading by rememberSaveable { mutableStateOf(true) }
  var hasError by rememberSaveable { mutableStateOf(false) }
  val webViewState = rememberSaveable(saver = LoginWebViewState.Saver) { LoginWebViewState() }

  Scaffold(modifier = modifier) { contentPadding ->
    Box(modifier = Modifier.padding(contentPadding).fillMaxSize()) {
      if (hasError) {
        Column(
          modifier = Modifier.align(Alignment.Center),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
        ) {
          Text("Failed to load login page.")
          Spacer(modifier = Modifier.height(12.dp))
          Button(
            onClick = {
              webViewState.reset()
              hasError = false
              isLoading = true
            }
          ) {
            Text("Retry")
          }
        }
      } else {
        AndroidView(
          factory = { context ->
            WebView(context)
              .apply {
                @SuppressLint("SetJavaScriptEnabled")
                settings.javaScriptEnabled = true
                // Keep the WebView User-Agent identical to the one [UserAgentInterceptor]
                // sends on OkHttp requests. Anubis embeds the requesting User-Agent in the
                // challenge metadata and issues challenge cookies keyed to it, so presenting
                // the system WebView default User-Agent here would yield cookies that the
                // data API requests don't get accepted with.
                settings.userAgentString = CHROME_USER_AGENT
                webViewClient =
                  object : WebViewClient() {
                    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                      isLoading = true
                    }

                    override fun onPageFinished(view: WebView, url: String) {
                      isLoading = false
                      if (!url.contains("/login")) {
                        @SuppressLint("DeprecatedCall")
                        view.evaluateJavascript(
                          "document.body && document.body.getAttribute('data-username')"
                        ) { usernameResult ->
                          val username = parseAuthenticatedUsername(usernameResult)
                          if (username != null) {
                            val cookie = CookieManager.getInstance().getCookie(LOBSTERS_URL)
                            if (cookie != null) {
                              onLoginSuccess(cookie, username)
                              popBackStack()
                            }
                          }
                        }
                      }
                    }

                    override fun onReceivedError(
                      view: WebView,
                      request: WebResourceRequest,
                      error: WebResourceError,
                    ) {
                      if (request.isForMainFrame) {
                        isLoading = false
                        hasError = true
                      }
                    }
                  }
                @SuppressLint("DeprecatedCall")
                val restoredHistory = webViewState.restoredState?.let { restoreState(it) }
                if (restoredHistory == null) {
                  loadUrl(LOGIN_URL)
                }
              }
              .also { webViewState.webView = it }
          },
          modifier = Modifier.fillMaxSize(),
        )
      }

      if (isLoading && !hasError) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter))
      }
    }
  }
}
