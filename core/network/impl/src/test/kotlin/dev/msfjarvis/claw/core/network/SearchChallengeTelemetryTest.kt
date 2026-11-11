/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.network

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

private data class SearchReport(val challenged: Boolean, val authenticated: Boolean)

class SearchChallengeTelemetryTest {
  private val challenge =
    """<title>Making sure you&#39;re not a bot!</title><link href="/.within.website/x/xess/xess.min.css">"""
  private val sessionStore =
    object : SessionCookieStore {
      override fun get(): String? = null

      override fun getUsername(): String? = null

      override fun set(cookie: String, username: String) = Unit

      override fun clear() = Unit

      override fun isLoggedIn(): Flow<Boolean> = error("Not used")

      override fun username(): Flow<String?> = error("Not used")
    }

  @Test
  fun `challenge is counted and reported as an error`() {
    val reports = mutableListOf<SearchReport>()
    assertThrows<SearchChallengeException> {
      execute("https://lobste.rs/search?q=private", challenge, reports)
    }
    assertThat(reports).containsExactly(SearchReport(challenged = true, authenticated = false))
  }

  @Test
  fun `normal search is counted separately`() {
    val reports = mutableListOf<SearchReport>()
    execute("https://lobste.rs/search?q=rust", "<title>Search | Lobsters</title>", reports).use {
      assertThat(it.body.string()).isEqualTo("<title>Search | Lobsters</title>")
    }
    assertThat(reports).containsExactly(SearchReport(challenged = false, authenticated = false))
    assertThat(isAnubisChallenge("<title>Making sure you're not a bot!</title>")).isFalse()
    assertThat(isAnubisChallenge(challenge.replace("&#39;", "'"))).isTrue()
  }

  @Test
  fun `other paths hosts and response types are not counted`() {
    val reports = mutableListOf<SearchReport>()
    execute("https://lobste.rs/", challenge, reports).close()
    execute("https://example.com/search", challenge, reports).close()
    execute("https://lobste.rs/search", challenge, reports, contentType = "application/json")
      .close()
    assertThat(reports).isEmpty()
  }

  @Test
  fun `challenge on an error status is also counted`() {
    val reports = mutableListOf<SearchReport>()
    assertThrows<SearchChallengeException> {
      execute("https://lobste.rs/search", challenge, reports, status = 403)
    }
    assertThat(reports).containsExactly(SearchReport(challenged = true, authenticated = false))
  }

  private fun execute(
    url: String,
    html: String,
    reports: MutableList<SearchReport>,
    contentType: String = "text/html; charset=utf-8",
    status: Int = 200,
  ): Response {
    val client =
      OkHttpClient.Builder()
        .addInterceptor(
          SearchChallengeInterceptor(
            sessionStore,
            SearchChallengeTelemetry { challenged, authenticated ->
              reports += SearchReport(challenged = challenged, authenticated = authenticated)
            },
          )
        )
        .addInterceptor { chain ->
          Response.Builder()
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .code(status)
            .message("test")
            .header("Content-Type", contentType)
            .body(html.toResponseBody(contentType.toMediaType()))
            .build()
        }
        .build()
    return client.newCall(Request.Builder().url(url).build()).execute()
  }
}
