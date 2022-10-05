package dev.msfjarvis.claw.metadata

import com.chimbori.crux.Crux
import com.chimbori.crux.api.Fields.CANONICAL_URL
import com.chimbori.crux.api.Fields.DURATION_MS
import com.chimbori.crux.api.Fields.FAVICON_URL
import dev.msfjarvis.claw.model.LinkMetadata
import javax.inject.Inject
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

class MetadataExtractor
@Inject
constructor(
  private val crux: Crux,
  private val okHttpClient: OkHttpClient,
) {

  suspend fun getExtractedMetadata(url: String): LinkMetadata {
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
      val faviconUrl = extractedMetadata.urls[FAVICON_URL].toString()
      val readingTime = extractedMetadata[DURATION_MS]
      val overriddenUrl = extractedMetadata[CANONICAL_URL] ?: url
      LinkMetadata(
        url = overriddenUrl,
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
