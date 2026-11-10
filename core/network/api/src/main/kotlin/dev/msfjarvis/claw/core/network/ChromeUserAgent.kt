/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.network

private const val DEFAULT_BROWSER_VERSION = "100.0.0.0"

/**
 * The browser User-Agent feigned for all HTTP requests.
 *
 * Must stay in sync with any embedded [android.webkit.WebView] that is expected to produce Anubis
 * challenge cookies on behalf of this client: Anubis embeds the requesting User-Agent inside the
 * challenge metadata and keys the resulting auth cookie to it, so a WebView presenting a different
 * User-Agent (e.g. the system WebView default) would obtain challenge cookies that the OkHttp
 * client, which presents this User-Agent, is not trusted with.
 */
const val CHROME_USER_AGENT =
  "Mozilla/5.0 (Linux; Android 11; Build/RQ2A.210505.003) AppleWebKit/537.36 " +
    "(KHTML, like Gecko) Version/4.0 Chrome/$DEFAULT_BROWSER_VERSION Mobile Safari/537.36"
