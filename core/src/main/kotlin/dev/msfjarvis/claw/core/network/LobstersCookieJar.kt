/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.network

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.Headers
import okhttp3.HttpUrl

private const val LOBSTERS_HOST = "lobste.rs"

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class LobstersCookieJar(private val store: SessionCookieStore) : CookieJar {

  override fun loadForRequest(url: HttpUrl): List<Cookie> {
    if (url.host != LOBSTERS_HOST) return emptyList()
    val raw = store.get() ?: return emptyList()
    val headers = Headers.headersOf("Set-Cookie", raw)
    return Cookie.parseAll(url, headers)
  }

  override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
    if (url.host != LOBSTERS_HOST) return
    if (cookies.isEmpty()) return
    // Serialize back to a single Set-Cookie header value using the first matching cookie.
    // lobste.rs sets one session cookie (lobsters_trap); joining handles edge cases.
    val raw = cookies.joinToString("; ") { "${it.name}=${it.value}" }
    store.set(raw)
  }
}
