/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api

import com.slack.eithernet.integration.retrofit.ApiResultCallAdapterFactory
import com.slack.eithernet.integration.retrofit.ApiResultConverterFactory
import com.slack.eithernet.test.EitherNetController
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

class SearchApiWrapper(controller: EitherNetController<LobstersSearchApi>) {
  val api: LobstersSearchApi

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
        .addCallAdapterFactory(ApiResultCallAdapterFactory)
        .build()
    api = retrofit.create()
  }

  private class FixtureInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
      val request = chain.request()
      return Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .code(200)
        .message("OK")
        .body(
          getResource("search_chatgpt_page.html")
            .toByteArray()
            .toResponseBody("text/html; charset=utf-8".toMediaType())
        )
        .build()
    }
  }
}
