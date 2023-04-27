/*
 * Copyright © 2022-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.network

import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

/** An OkHttp [Interceptor] that feigns a browser User-Agent for all requests. */
class UserAgentInterceptor @Inject constructor() : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    return chain.proceed(
      chain.request().newBuilder().header("User-Agent", CHROME_USER_AGENT).build()
    )
  }

  private companion object {
    private const val DEFAULT_BROWSER_VERSION = "100.0.0.0"
    private const val CHROME_USER_AGENT =
      "Mozilla/5.0 (Linux; Android 11; Build/RQ2A.210505.003) AppleWebKit/537.36 " +
        "(KHTML, like Gecko) Version/4.0 Chrome/$DEFAULT_BROWSER_VERSION Mobile Safari/537.36"
  }
}
