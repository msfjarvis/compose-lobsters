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
import java.util.concurrent.TimeUnit
import kotlinx.datetime.format.DateTimeComponents
import okhttp3.Interceptor
import okhttp3.Response

/**
 * An OkHttp [Interceptor] that respects the Retry-After header sent by Lobsters when rate limiting
 * is triggered. This handles both HTTP 429 (Too Many Requests) and other status codes that may
 * include the Retry-After header.
 */
@ContributesIntoSet(AppScope::class)
@Inject
class RetryAfterInterceptor : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()
    val response = chain.proceed(request)

    // Check if response includes Retry-After header
    val retryAfter = response.header("Retry-After")
    if (retryAfter != null && shouldRetry(response.code)) {
      val delaySeconds = parseRetryAfter(retryAfter)
      if (delaySeconds > 0) {
        // Close the initial response
        response.close()

        // Wait for the specified delay
        TimeUnit.SECONDS.sleep(delaySeconds)

        // Retry the request
        return chain.proceed(request)
      }
    }

    return response
  }

  /**
   * Determines if the response status code should trigger a retry.
   *
   * Common status codes that use Retry-After:
   * - 429 Too Many Requests (rate limiting)
   * - 503 Service Unavailable
   */
  private fun shouldRetry(statusCode: Int): Boolean {
    return statusCode == 429 || statusCode == 503
  }

  /**
   * Parses the Retry-After header value, which can be either:
   * 1. A delay in seconds (e.g., "120")
   * 2. An HTTP-date (e.g., "Wed, 21 Oct 2015 07:28:00 GMT")
   *
   * Returns the delay in seconds, or 0 if parsing fails.
   */
  private fun parseRetryAfter(retryAfter: String): Long {
    retryAfter.toLongOrNull()?.let {
      return it.coerceAtMost(MAX_RETRY_DELAY_SECONDS)
    }

    val retryInstant = parseHttpDate(retryAfter) ?: return 0
    val delaySeconds = (retryInstant.toEpochMilliseconds() - System.currentTimeMillis()) / 1000
    return if (delaySeconds > 0) delaySeconds.coerceAtMost(MAX_RETRY_DELAY_SECONDS) else 0
  }

  private fun parseHttpDate(value: String) =
    try {
      DateTimeComponents.Formats.RFC_1123.parse(value).toInstantUsingOffset()
    } catch (_: IllegalArgumentException) {
      null
    }

  private companion object {
    private const val MAX_RETRY_DELAY_SECONDS = 300L
  }
}
