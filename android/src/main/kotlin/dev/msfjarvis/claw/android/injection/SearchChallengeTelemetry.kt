/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.injection

import dev.msfjarvis.claw.core.network.SessionCookieStore
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import io.sentry.Sentry
import io.sentry.SentryAttribute
import io.sentry.SentryAttributes
import io.sentry.metrics.SentryMetricsParameters
import okhttp3.Interceptor
import okhttp3.Response

@BindingContainer
@ContributesTo(AppScope::class)
object SearchChallengeTelemetry {
  @Provides
  @IntoSet
  fun interceptor(store: SessionCookieStore): Interceptor =
    SearchChallengeInterceptor { challenged ->
      val attributes =
        SentryAttributes.of(
          SentryAttribute.stringAttribute("outcome", if (challenged) "challenge" else "other"),
          SentryAttribute.stringAttribute(
            "session",
            if (store.getUsername() == null) "anonymous" else "authenticated",
          ),
        )
      Sentry.metrics()
        .count("search_html_response", 1.0, null, SentryMetricsParameters.create(attributes))
    }
}

internal class SearchChallengeInterceptor(private val report: (Boolean) -> Unit) : Interceptor {
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
      val html = response.peekBody(64 * 1024L).string()
      report(isAnubisChallenge(html))
    }
    return response
  }
}

internal fun isAnubisChallenge(html: String): Boolean =
  html.contains("/.within.website/x/", ignoreCase = true) &&
    (html.contains("Making sure you&#39;re not a bot!", ignoreCase = true) ||
      html.contains("Making sure you're not a bot!", ignoreCase = true))
