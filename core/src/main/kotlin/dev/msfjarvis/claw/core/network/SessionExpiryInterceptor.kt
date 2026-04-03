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

/**
 * Detects an expired session by checking if OkHttp was redirected to the login page. Because
 * [okhttp3.OkHttpClient] follows redirects automatically, the 3xx response is never visible here —
 * instead we check the final request URL after redirect following. When expiry is detected the
 * stale cookie is cleared and the request is retried once, unauthenticated.
 */
@ContributesIntoSet(AppScope::class)
@Inject
class SessionExpiryInterceptor(private val store: SessionCookieStore) : Interceptor {

  override fun intercept(chain: Interceptor.Chain): Response {
    val response = chain.proceed(chain.request())

    val finalPath = response.request.url.pathSegments
    val redirectedToLogin = finalPath.contains("login")

    if (redirectedToLogin && store.get() != null) {
      store.clear()
      response.close()
      // One unauthenticated retry — no loop.
      return chain.proceed(chain.request())
    }

    return response
  }
}
