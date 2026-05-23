/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import android.webkit.CookieManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

interface WebViewCookieStore {
  fun clearLobstersCookies()
}

@Inject
@ContributesBinding(AppScope::class)
class AndroidWebViewCookieStore : WebViewCookieStore {
  override fun clearLobstersCookies() {
    CookieManager.getInstance().removeAllCookies(null)
    CookieManager.getInstance().flush()
  }
}
