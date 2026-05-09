/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api.converters

import com.fleeksoft.ksoup.Ksoup
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.api.ReplyForm
import java.lang.reflect.Type
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit

object ReplyFormConverter : Converter<ResponseBody, ReplyForm> {
  override fun convert(value: ResponseBody): ReplyForm {
    val document = Ksoup.parse(value.string(), baseUri = LobstersApi.BASE_URL)
    fun inputValue(name: String): String =
      document.select("input[name=\"$name\"]").firstOrNull()?.attr("value").orEmpty()

    return ReplyForm(
      authenticityToken = inputValue("authenticity_token"),
      storyId = inputValue("story_id"),
      method = inputValue("_method"),
      parentCommentShortId = inputValue("parent_comment_short_id"),
    )
  }

  object Factory : Converter.Factory() {
    override fun responseBodyConverter(
      type: Type,
      annotations: Array<out Annotation>,
      retrofit: Retrofit,
    ): Converter<ResponseBody, *>? {
      return if (type == ReplyForm::class.java) ReplyFormConverter else null
    }
  }
}
