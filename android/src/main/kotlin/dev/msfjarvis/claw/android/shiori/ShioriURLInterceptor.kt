/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.shiori

import javax.inject.Inject
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response

/**
 * [Interceptor] that dynamically replaces the base URL of a [okhttp3.Request] with the
 * user-provided Shiori base URL for each request. This is necessary since the URL can change at
 * runtime and thus cannot be provided by the DI graph.
 */
class ShioriURLInterceptor
@Inject
constructor(
  private val shioriSettings: ShioriSettings,
) : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val host = shioriSettings.getUrl()
    var request = chain.request()
    if (host != null && request.url.host == "example.com") {
      val newUrl: HttpUrl = request.url.newBuilder().host(host.toHttpUrl().host).build()
      request = request.newBuilder().url(newUrl).build()
    }
    return chain.proceed(request)
  }
}
