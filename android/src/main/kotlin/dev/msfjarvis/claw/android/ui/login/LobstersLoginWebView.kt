/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.login

import android.webkit.CookieManager
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import dev.msfjarvis.claw.api.LobstersApi
import io.github.aakira.napier.Napier
import okhttp3.HttpUrl.Companion.toHttpUrl

@Suppress("Unused")
@Composable
fun LobstersLoginWebView(
  modifier: Modifier = Modifier,
) {
  val state = rememberWebViewState(url = "https://lobste.rs/login")
  val webClient = remember {
    object : AccompanistWebViewClient() {
      override fun onPageFinished(view: WebView, url: String?) {
        super.onPageFinished(view, url)
        Napier.d(tag = "WebView") { "onPageFinished(url=$url)" }
        if (url != null && url.toHttpUrl() == LobstersApi.BASE_URL.toHttpUrl()) {
          val cookies = CookieManager.getInstance().getCookie(LobstersApi.BASE_URL)
          val trap =
            cookies
              .split(";")
              .associate {
                val split = it.split("=")
                split[0] to split[1]
              }["lobsters_trap"]
          Napier.d(tag = "WebView") { "lobsters_trap=${trap}" }
        }
      }
    }
  }
  WebView(
    state = state,
    client = webClient,
    modifier = modifier,
    captureBackPresses = false,
  )
}
