/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.screens

import android.graphics.Bitmap
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

private const val LOGIN_URL = "https://lobste.rs/login"
private const val LOBSTERS_URL = "https://lobste.rs"

@Composable
fun LoginScreen(
  onLoginSuccess: (String) -> Unit,
  popBackStack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var isLoading by remember { mutableStateOf(true) }
  var hasError by remember { mutableStateOf(false) }
  var webViewRef by remember { mutableStateOf<WebView?>(null) }

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
              hasError = false
              isLoading = true
              webViewRef?.loadUrl(LOGIN_URL)
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
                settings.javaScriptEnabled = true
                webViewClient =
                  object : WebViewClient() {
                    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                      isLoading = true
                    }

                    override fun onPageFinished(view: WebView, url: String) {
                      isLoading = false
                      // Both /login and /login/2fa contain "/login" — only act when
                      // navigating away from both (i.e., successful authentication).
                      // CookieManager requires the full URL scheme+host to return cookies.
                      if (!url.contains("/login")) {
                        val cookie = CookieManager.getInstance().getCookie(LOBSTERS_URL)
                        if (cookie != null) {
                          onLoginSuccess(cookie)
                        }
                        popBackStack()
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
                loadUrl(LOGIN_URL)
              }
              .also { webViewRef = it }
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
