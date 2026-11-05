/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("DeprecatedCall")

package dev.msfjarvis.claw.android.ui.screens

import android.annotation.SuppressLint
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.msfjarvis.claw.android.R
import dev.msfjarvis.claw.core.network.CHROME_USER_AGENT
import kotlinx.coroutines.delay

private const val ORIGIN = "https://lobste.rs"
private const val CHALLENGE_URL = "$ORIGIN/search?what=stories&order=newest&q=rust&page=1"

@Composable
internal fun AnubisChallengeDialog(onCookie: (String) -> Unit, onDismiss: () -> Unit) {
  val manager = remember { CookieManager.getInstance() }
  val previousCookies = remember { manager.getCookie(ORIGIN) }
  var webView by remember { mutableStateOf<WebView?>(null) }
  var hasError by remember { mutableStateOf(false) }
  val currentOnCookie by rememberUpdatedState(onCookie)

  LaunchedEffect(webView) {
    val view = webView ?: return@LaunchedEffect
    while (true) {
      val url = view.url?.let(android.net.Uri::parse)
      if (url?.scheme == "https" && url.host == "lobste.rs") {
        val cookie = newAnubisClearanceCookie(manager.getCookie(ORIGIN), previousCookies)
        if (cookie != null) {
          currentOnCookie(cookie)
          break
        }
      }
      delay(500)
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Surface(modifier = Modifier.fillMaxSize()) {
      Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = onDismiss) { Text(stringResource(R.string.challenge_cancel)) }
        if (hasError) {
          Text(stringResource(R.string.challenge_load_error))
          Button(
            onClick = {
              hasError = false
              webView?.loadUrl(CHALLENGE_URL)
            }
          ) {
            Text(stringResource(R.string.challenge_retry))
          }
        }
        AndroidView(
          modifier = Modifier.fillMaxWidth().weight(1f),
          factory = { context ->
            WebView(context)
              .apply {
                @SuppressLint("SetJavaScriptEnabled")
                settings.javaScriptEnabled = true
                settings.userAgentString = CHROME_USER_AGENT
                webViewClient =
                  object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                      view: WebView,
                      request: WebResourceRequest,
                    ): Boolean = request.url.scheme != "https" || request.url.host != "lobste.rs"

                    override fun onReceivedError(
                      view: WebView,
                      request: WebResourceRequest,
                      error: WebResourceError,
                    ) {
                      if (request.isForMainFrame) hasError = true
                    }
                  }
                loadUrl(CHALLENGE_URL)
              }
              .also { webView = it }
          },
          onRelease = { it.destroy() },
        )
      }
    }
  }
}
