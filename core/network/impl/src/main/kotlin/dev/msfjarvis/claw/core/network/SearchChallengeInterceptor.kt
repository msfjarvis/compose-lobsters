/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.network

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import okhttp3.Interceptor
import okhttp3.Response

@ContributesIntoSet(AppScope::class)
@Inject
class SearchChallengeInterceptor(
  private val store: SessionCookieStore,
  private val telemetry: SearchChallengeTelemetry,
) : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()
    val response = chain.proceed(request)
    if (
      request.method == "GET" &&
        request.url.host == "lobste.rs" &&
        request.url.encodedPath == "/search" &&
        response.request.url.host == "lobste.rs" &&
        response.header("Content-Type")?.startsWith("text/html", ignoreCase = true) == true
    ) {
      val challenged = isAnubisChallenge(response.peekBody(64 * 1024L).string())
      telemetry.report(challenged, store.getUsername() != null)
      if (challenged) {
        response.close()
        throw SearchChallengeException()
      }
    }
    return response
  }
}

internal fun isAnubisChallenge(html: String): Boolean =
  html.contains("/.within.website/x/", ignoreCase = true) &&
    (html.contains("Making sure you&#39;re not a bot!", ignoreCase = true) ||
      html.contains("Making sure you're not a bot!", ignoreCase = true))
