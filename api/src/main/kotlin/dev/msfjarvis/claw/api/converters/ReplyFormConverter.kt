/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api.converters

import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.api.ReplyForm
import okhttp3.ResponseBody
import org.jsoup.Jsoup

object ReplyFormConverter {
  fun convert(value: ResponseBody): ReplyForm {
    val document = Jsoup.parse(value.string(), LobstersApi.BASE_URL)
    fun inputValue(name: String): String =
      document.select("input[name=\"$name\"]").firstOrNull()?.attr("value").orEmpty()

    return ReplyForm(
      authenticityToken = inputValue("authenticity_token"),
      storyId = inputValue("story_id"),
      method = inputValue("_method"),
      parentCommentShortId = inputValue("parent_comment_short_id"),
    )
  }
}
