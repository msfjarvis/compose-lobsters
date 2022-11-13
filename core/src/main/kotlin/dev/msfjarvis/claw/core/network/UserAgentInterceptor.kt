/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.network

import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

/** An OkHttp [Interceptor] that adds a recognizable User-Agent header to all network requests. */
class UserAgentInterceptor @Inject constructor() : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    return chain.proceed(chain.request().newBuilder().header("User-Agent", "Claw-Android").build())
  }
}
