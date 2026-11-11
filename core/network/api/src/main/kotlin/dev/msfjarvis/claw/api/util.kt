/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api

import com.slack.eithernet.ApiResult.Failure
import java.io.IOException
import java.net.HttpURLConnection

@Suppress("NOTHING_TO_INLINE") // We inline this to eliminate the stacktrace frame.
inline fun <T : Any> Failure.HttpFailure<T>.toError(): Throwable =
  when (code) {
    HttpURLConnection.HTTP_NOT_FOUND -> IOException("Story was removed by moderator")
    HttpURLConnection.HTTP_INTERNAL_ERROR,
    HttpURLConnection.HTTP_BAD_GATEWAY,
    HttpURLConnection.HTTP_UNAVAILABLE -> IOException("It appears lobste.rs is currently down")
    else -> IOException("API returned an invalid response")
  }
