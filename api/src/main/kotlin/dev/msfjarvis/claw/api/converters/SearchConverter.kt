/*
 * Copyright © 2023-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api.converters

import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.model.LobstersPost
import java.lang.reflect.Type
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import retrofit2.Converter
import retrofit2.Retrofit

object SearchConverter : Converter<ResponseBody, List<LobstersPost>> {
  override fun convert(value: ResponseBody): List<LobstersPost> {
    val elements =
      Jsoup.parse(value.string(), LobstersApi.BASE_URL).select("div.story_liner.h-entry")
    return elements.map(::parsePost)
  }

  private fun parsePost(elem: Element): LobstersPost {
    val parent = elem.parent() ?: error("$elem must have a parent")
    val shortId = parent.attr("data-shortid")
    val titleElement = elem.select("span.link.h-cite > a")
    val title = titleElement.text()
    val url = titleElement.attr("href")
    val tags = elem.select("span.tags > a").map(Element::text)
    val (commentCount, commentsUrl) = getCommentsData(elem.select("span.comments_label"))
    val submitter = elem.select("div.byline > a.u-author").text()
    return LobstersPost(
      shortId = shortId,
      title = title,
      url = url,
      commentCount = commentCount,
      commentsUrl = commentsUrl,
      tags = tags,
      submitter = submitter,
      // The value of these fields is irrelevant for our use case
      createdAt = "",
      description = "",
    )
  }

  private fun getCommentsData(elem: Elements): Pair<Int, String> {
    val linkElement = elem.select("a")
    val countString = linkElement.text().trimStart().substringBefore(" ")
    val commentsUrl = LobstersApi.BASE_URL + linkElement.attr("href")
    return (countString.toIntOrNull() ?: 0) to commentsUrl
  }

  object Factory : Converter.Factory() {
    override fun responseBodyConverter(
      type: Type,
      annotations: Array<out Annotation>,
      retrofit: Retrofit,
    ): Converter<ResponseBody, List<LobstersPost>> {
      return SearchConverter
    }
  }
}
