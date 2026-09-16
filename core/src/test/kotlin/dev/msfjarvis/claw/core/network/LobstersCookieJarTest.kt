/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.network

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.jupiter.api.Test

class LobstersCookieJarTest {

  private val url = "https://lobste.rs".toHttpUrl()

  private fun cookie(setCookieHeader: String): Cookie {
    return Cookie.parse(url, setCookieHeader)!!
  }

  @Test
  fun `parseCookies recovers every cookie from a serialized list`() {
    val raw =
      "techaro.lol-anubis-auth=jwt$COOKIE_SEPARATOR" + "techaro.lol-anubis-cookie-verification=uuid"

    val cookies = parseCookies(url, raw)

    assertThat(cookies.map { it.name })
      .containsExactly(
        "techaro.lol-anubis-auth",
        "techaro.lol-anubis-cookie-verification",
      )
    assertThat(cookies.map { it.value }).containsExactly("jwt", "uuid")
  }

  @Test
  fun `parseCookies handles the semicolon-joined format from CookieManager`() {
    // Cookies captured by android.webkit.CookieManager#getCookie are joined with "; ".
    val raw = "techaro.lol-anubis-auth=jwt; techaro.lol-anubis-cookie-verification=uuid"

    val cookies = parseCookies(url, raw)

    assertThat(cookies.map { it.name to it.value })
      .containsExactly(
        "techaro.lol-anubis-auth" to "jwt",
        "techaro.lol-anubis-cookie-verification" to "uuid",
      )
  }

  @Test
  fun `serializeCookies round-trips through parseCookies`() {
    val cookies =
      listOf(
        cookie("lobsters_trap=session"),
        cookie("techaro.lol-anubis-auth=jwt"),
        cookie("techaro.lol-anubis-cookie-verification=uuid"),
      )

    val roundTripped = parseCookies(url, serializeCookies(cookies))

    assertThat(roundTripped.map { it.name to it.value })
      .containsExactly(
        "lobsters_trap" to "session",
        "techaro.lol-anubis-auth" to "jwt",
        "techaro.lol-anubis-cookie-verification" to "uuid",
      )
  }

  @Test
  fun `mergeCookies preserves unrelated stored cookies when only one is updated`() {
    val now = 1_000_000L
    val stored =
      listOf(
        cookie("lobsters_trap=session"),
        cookie("techaro.lol-anubis-auth=jwt"),
        cookie("techaro.lol-anubis-cookie-verification=old-uuid"),
      )
    val incoming = listOf(cookie("techaro.lol-anubis-cookie-verification=new-uuid"))

    val merged = mergeCookies(stored, incoming, now)

    assertThat(merged.map { it.name to it.value })
      .containsExactly(
        "lobsters_trap" to "session",
        "techaro.lol-anubis-auth" to "jwt",
        "techaro.lol-anubis-cookie-verification" to "new-uuid",
      )
  }

  @Test
  fun `mergeCookies lets incoming cookies override stored ones with the same name`() {
    val now = 1_000_000L
    val stored = listOf(cookie("lobsters_trap=old-session"))
    val incoming = listOf(cookie("lobsters_trap=new-session"))

    val merged = mergeCookies(stored, incoming, now)

    assertThat(merged.map { it.name to it.value }).containsExactly("lobsters_trap" to "new-session")
  }

  @Test
  fun `mergeCookies drops cookies the server expired`() {
    val now = 2_000_000L
    val stored = listOf(cookie("lobsters_trap=session"))
    // Anubis clearing its challenge token: Max-Age=0 makes this cookie already-expired.
    val incoming = listOf(cookie("techaro.lol-anubis-auth=; Max-Age=0"))

    val merged = mergeCookies(stored, incoming, now)

    assertThat(merged.map { it.name }).containsExactly("lobsters_trap")
  }

  @Test
  fun `a cookie delta response does not clobber the stored session, and all cookies are loaded back`() {
    val store = FakeSessionCookieStore()
    store.set("lobsters_trap=session", "alice")
    val jar = LobstersCookieJar(store)

    // Anubis refreshes only its challenge-verification cookie in this response; the session
    // cookie is untouched but must survive the save rather than being wiped out.
    val delta = listOf(cookie("techaro.lol-anubis-cookie-verification=new-uuid"))
    jar.saveFromResponse(url, delta)

    assertThat(store.savedCookie).contains("lobsters_trap=session")
    assertThat(store.savedCookie).contains("techaro.lol-anubis-cookie-verification=new-uuid")

    val loaded = jar.loadForRequest(url)
    assertThat(loaded.map { it.name to it.value })
      .containsExactly(
        "lobsters_trap" to "session",
        "techaro.lol-anubis-cookie-verification" to "new-uuid",
      )
  }

  private class FakeSessionCookieStore : SessionCookieStore {
    var savedCookie: String? = null
    var cleared = false

    override fun get(): String? = savedCookie

    override fun getUsername(): String? = "alice"

    override fun set(cookie: String, username: String) {
      savedCookie = cookie
    }

    override fun clear() {
      cleared = true
      savedCookie = null
    }

    override fun isLoggedIn(): Flow<Boolean> = kotlinx.coroutines.flow.flowOf(!cleared)

    override fun username(): Flow<String?> = kotlinx.coroutines.flow.flowOf("alice")
  }
}
