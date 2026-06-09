/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api.converters

import dev.msfjarvis.claw.api.LobstersParserClient
import dev.msfjarvis.claw.model.CSRFToken
import dev.msfjarvis.claw.model.FiltersPage
import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.model.LobstersPostDetails
import dev.msfjarvis.claw.model.ReplyForm
import dev.msfjarvis.claw.model.Tag
import dev.msfjarvis.claw.model.User
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.http.GET

class ZiplineHtmlConverterFactory(private val parserClient: LobstersParserClient) :
  Converter.Factory() {
  override fun responseBodyConverter(
    type: Type,
    annotations: Array<Annotation>,
    retrofit: Retrofit,
  ): Converter<ResponseBody, *>? {
    val normalizedType = type.unwrapWildcard()
    val getPath = annotations.filterIsInstance<GET>().singleOrNull()?.value
    return when {
      normalizedType.isListOf(LobstersPost::class.java) &&
        getPath?.startsWith("/search") == true -> {
        HtmlConverter { html ->
          parserClient.service().parseSearchResults(html).map { it.toModel() }
        }
      }
      normalizedType.isListOf(LobstersPost::class.java) -> {
        HtmlConverter { html -> parserClient.service().parsePostsPage(html).map { it.toModel() } }
      }
      normalizedType.isListOf(Tag::class.java) -> {
        HtmlConverter { html -> parserClient.service().parseTagsPage(html).map { it.toModel() } }
      }
      normalizedType == CSRFToken::class.java ->
        HtmlConverter { html -> parserClient.service().parseCsrfToken(html).toModel() }
      normalizedType == LobstersPostDetails::class.java ->
        HtmlConverter { html -> parserClient.service().parsePostDetails(html).toModel() }
      normalizedType == ReplyForm::class.java ->
        HtmlConverter { html -> parserClient.service().parseReplyForm(html).toModel() }
      normalizedType == FiltersPage::class.java ->
        HtmlConverter { html -> parserClient.service().parseFiltersPage(html).toModel() }
      normalizedType == User::class.java ->
        HtmlConverter { html -> parserClient.service().parseUser(html).toModel() }
      else -> null
    }
  }

  private fun Type.unwrapWildcard(): Type {
    return if (this is WildcardType) this.upperBounds.singleOrNull() ?: this else this
  }

  private fun Type.isListOf(raw: Class<*>): Boolean {
    val normalized = unwrapWildcard()
    if (normalized !is ParameterizedType) return false
    val parameter = getParameterUpperBound(0, normalized).unwrapWildcard()
    return getRawType(normalized) == List::class.java && parameter == raw
  }

  private class HtmlConverter<T>(private val parse: suspend (String) -> T) :
    Converter<ResponseBody, T> {
    override fun convert(value: ResponseBody): T {
      value.use { body ->
        return runBlocking { parse(body.string()) }
      }
    }
  }
}
