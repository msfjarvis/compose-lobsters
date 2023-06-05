/*
 * Copyright © 2022-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import com.chimbori.crux.Crux
import com.chimbori.crux.api.Fields
import dev.msfjarvis.claw.model.LinkMetadata
import javax.inject.Inject
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

class LinkMetadataRepository
@Inject
constructor(
  private val crux: Crux,
  private val okHttpClient: OkHttpClient,
) {
  suspend fun getLinkMetadata(url: String): LinkMetadata {
    return run {
      val parsedUrl = url.toHttpUrlOrNull() ?: return@run null
      if (!parsedUrl.isHttps) return@run null
      val request = Request.Builder().url(parsedUrl).build()
      val htmlContent =
        okHttpClient.newCall(request).execute().use { response ->
          val body = response.body ?: return@run null
          body.string()
        }
      val extractedMetadata = crux.extractFrom(parsedUrl, Jsoup.parse(htmlContent, url))
      val faviconUrl = extractedMetadata[Fields.FAVICON_URL].toString()
      val readingTime = extractedMetadata[Fields.DURATION_MS].toString()
      LinkMetadata(
        url = url,
        faviconUrl = faviconUrl,
        readingTime = readingTime,
      )
    }
      ?: makeDefault(url)
  }

  private fun makeDefault(url: String) =
    LinkMetadata(
      url,
      null,
      null,
    )
}
