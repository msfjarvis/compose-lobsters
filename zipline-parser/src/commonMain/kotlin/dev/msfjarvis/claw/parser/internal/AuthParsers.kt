/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.parser.internal

import com.fleeksoft.ksoup.Ksoup
import dev.msfjarvis.claw.parser.model.CSRFToken
import dev.msfjarvis.claw.parser.model.ReplyForm

private const val BASE_URL = "https://lobste.rs"

internal fun parseCsrfToken(html: String): CSRFToken {
  val token =
    Ksoup.parse(html, baseUri = BASE_URL)
      .select("meta[name=csrf-token]")
      .firstOrNull()
      ?.attr("content")
      .orEmpty()
  return CSRFToken(token)
}

internal fun parseReplyForm(html: String): ReplyForm {
  val document = Ksoup.parse(html, baseUri = BASE_URL)
  fun inputValue(name: String): String =
    document.select("input[name=$name]").firstOrNull()?.attr("value").orEmpty()

  return ReplyForm(
    authenticityToken = inputValue("authenticity_token"),
    storyId = inputValue("story_id"),
    method = inputValue("_method"),
    parentCommentShortId = inputValue("parent_comment_short_id"),
  )
}
