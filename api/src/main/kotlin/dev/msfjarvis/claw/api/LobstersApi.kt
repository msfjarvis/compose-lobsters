/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api

import com.slack.eithernet.ApiResult
import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.model.LobstersPostDetails
import dev.msfjarvis.claw.model.User
import retrofit2.http.GET
import retrofit2.http.Path

/** Simple interface defining an API for lobste.rs */
interface LobstersApi {

  @GET("page/{page}.json")
  suspend fun getHottestPosts(@Path("page") page: Int): ApiResult<List<LobstersPost>, Unit>

  @GET("newest/page/{page}.json")
  suspend fun getNewestPosts(@Path("page") page: Int): ApiResult<List<LobstersPost>, Unit>

  @GET("s/{postId}.json")
  suspend fun getPostDetails(@Path("postId") postId: String): ApiResult<LobstersPostDetails, Unit>

  @GET("~{username}.json")
  suspend fun getUser(@Path("username") username: String): ApiResult<User, Unit>

  @GET("/") suspend fun getCSRFToken(): ApiResult<CSRFToken, Unit>

  companion object {
    const val BASE_URL = "https://lobste.rs"
  }
}
