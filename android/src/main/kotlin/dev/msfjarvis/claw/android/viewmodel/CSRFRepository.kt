/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import dev.msfjarvis.claw.api.injection.BaseUrl
import dev.msfjarvis.claw.core.injection.IODispatcher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

/** Helper for extracting CSRF token for authenticated requests to https://lobste.rs. */
class CSRFRepository
@Inject
constructor(
  private val okHttpClient: OkHttpClient,
  @IODispatcher private val dispatcher: CoroutineDispatcher,
  @BaseUrl private val url: String,
) {
  suspend fun extractToken(): String? {
    val request = Request.Builder().url(url).get().build()
    return withContext(dispatcher) {
      okHttpClient.newCall(request).execute().use { response ->
        val doc = Jsoup.parse(response.body?.string() ?: return@use null)
        val element = doc.select("meta[name=\"csrf-token\"]").first() ?: return@use null
        return@use element.attr("content")
      }
    }
  }
}
