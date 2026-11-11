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

/** Separator used to store multiple cookies in the single [SessionCookieStore] slot. */
internal const val COOKIE_SEPARATOR = "\n"

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class LobstersCookieJar(private val store: SessionCookieStore) : CookieJar {

  override fun loadForRequest(url: HttpUrl): List<Cookie> {
    if (url.host != LOBSTERS_HOST) return emptyList()
    val raw = store.get() ?: return emptyList()
    return parseCookies(url, raw)
  }

  override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
    if (url.host != LOBSTERS_HOST) return
    if (cookies.isEmpty()) return

    val username = store.getUsername().orEmpty()
    val currentlyStored = store.get()?.let { parseCookies(url, it) }.orEmpty()
    val merged = mergeCookies(currentlyStored, cookies, now = System.currentTimeMillis())

    if (merged.isEmpty()) {
      store.clear()
    } else {
      store.set(serializeCookies(merged), username)
    }
  }
}

/**
 * Parses a stored cookie list into individual cookies.
 *
 * Each store entry is normally a `name=value` pair; the outbound [CookieJar] serializes them
 * newline-separated, but cookies captured from Android's `CookieManager` (the login flow) arrive
 * joined with `"; "`. A single stored string may therefore mix both, so each entry is split on both
 * separators before being fed to OkHttp as its own `Set-Cookie` header value.
 */
internal fun parseCookies(url: HttpUrl, raw: String): List<Cookie> {
  val lines = raw.split(COOKIE_SEPARATOR).flatMap { it.split("; ") }
  val builder = Headers.Builder()
  lines.filter { it.isNotBlank() }.forEach { line -> builder.add("Set-Cookie", line) }
  return Cookie.parseAll(url, builder.build())
}

/** Serializes cookies into the single [COOKIE_SEPARATOR]-joined string used for storage. */
internal fun serializeCookies(cookies: List<Cookie>): String {
  return cookies.joinToString(COOKIE_SEPARATOR) { "${it.name}=${it.value}" }
}

/**
 * Merges a batch of freshly-received cookies into the currently-stored set.
 *
 * OkHttp hands [CookieJar.saveFromResponse] only the cookies *changed* by a single response, not a
 * full echo of the cookie store. Lobste.rs now sits behind Anubis, which issues several cookies
 * (`techaro.lol-anubis-auth`, `techaro.lol-anubis-cookie-verification`, ...) that are refreshed
 * independently of the authenticated `lobsters_trap` session cookie, and which it only sends back
 * as deltas. Blindly replacing the entire store with the incoming batch would therefore drop
 * unrelated cookies whenever a single one is (re)issued — silently logging the user out.
 *
 * Incoming cookies win over previously-stored ones with the same name, and cookies the server
 * deleted (expired, e.g. an Anubis challenge clearing its challenge token) are removed.
 */
internal fun mergeCookies(
  current: List<Cookie>,
  incoming: List<Cookie>,
  now: Long,
): List<Cookie> {
  val merged = LinkedHashMap<String, Cookie>()
  current.forEach { merged[it.name] = it }
  incoming.forEach { merged[it.name] = it }
  return merged.values.filterNot { cookie ->
    cookie.expiresAt != Long.MAX_VALUE && cookie.expiresAt <= now
  }
}
