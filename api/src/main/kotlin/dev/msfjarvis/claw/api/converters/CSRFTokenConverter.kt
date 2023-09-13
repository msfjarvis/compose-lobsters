/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api.converters

import dev.msfjarvis.claw.api.CSRFToken
import dev.msfjarvis.claw.api.LobstersApi
import java.lang.reflect.Type
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Converter
import retrofit2.Retrofit

object CSRFTokenConverter : Converter<ResponseBody, CSRFToken> {
  override fun convert(value: ResponseBody): CSRFToken {
    val token =
      Jsoup.parse(value.string(), LobstersApi.BASE_URL)
        .select("meta[name=\"csrf-token\"]")
        .first()!!
        .attr("content")
    return CSRFToken(token)
  }

  object Factory : Converter.Factory() {
    override fun responseBodyConverter(
      type: Type,
      annotations: Array<out Annotation>,
      retrofit: Retrofit,
    ): Converter<ResponseBody, CSRFToken> {
      return CSRFTokenConverter
    }
  }
}
