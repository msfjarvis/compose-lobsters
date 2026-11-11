/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.auth.api

/** Clears authentication cookies retained by the platform WebView. */
fun interface WebViewCookieStore {
  fun clearLobstersCookies()
}
