/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api

import com.slack.eithernet.integration.retrofit.ApiResultCallAdapterFactory
import com.slack.eithernet.integration.retrofit.ApiResultConverterFactory
import dev.msfjarvis.claw.api.converters.UnitConverter
import dev.msfjarvis.claw.api.converters.ZiplineHtmlConverterFactory
import dev.msfjarvis.claw.parser.LobstersParserService
import dev.msfjarvis.claw.parser.LobstersParserServiceImpl
import dev.msfjarvis.claw.util.TestUtils.getResource
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Retrofit
import retrofit2.create

class ApiWrapper {
  private val parser = LobstersParserServiceImpl()
  val upvotedPostDetails = parser.parsePostDetails(getResource("post_details_upvoted.html"))

  val api: LobstersApi
  val authenticatedApi: AuthenticatedLobstersApi

  init {
    val parserClient =
      object : LobstersParserClient {
        override suspend fun service(): LobstersParserService = LobstersParserServiceImpl()
      }
    val retrofit =
      Retrofit.Builder()
        .baseUrl("https://lobste.rs/")
        .client(OkHttpClient.Builder().addInterceptor(FixtureInterceptor()).build())
        .addConverterFactory(ApiResultConverterFactory)
        .addConverterFactory(ZiplineHtmlConverterFactory(parserClient))
        .addConverterFactory(UnitConverter.Factory)
        .addCallAdapterFactory(ApiResultCallAdapterFactory)
        .build()
    api = retrofit.create()
    authenticatedApi = AuthenticatedLobstersApi(api)
  }

  private class FixtureInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
      val request = chain.request()
      val body =
        when (request.url.encodedPath) {
          "/page/1" -> getResource("hottest_page.html")
          "/newest/page/1" -> getResource("hottest_page.html")
          "/s/tdfoqh" -> getResource("post_details_tdfoqh.html")
          "/~msfjarvis" -> getResource("msfjarvis.html")
          "/" -> getResource("csrf_page.html")
          "/tags" -> getResource("tags.html")
          "/comments/edtrox/reply" -> getResource("reply_form.html")
          else -> ""
        }
      return Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .code(200)
        .message("OK")
        .body(body.toByteArray().toResponseBody("text/html; charset=utf-8".toMediaType()))
        .build()
    }
  }
}
