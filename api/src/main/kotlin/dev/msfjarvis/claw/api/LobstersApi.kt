/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api

import com.slack.eithernet.ApiResult
import dev.msfjarvis.claw.api.util.Tags
import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.model.LobstersPostDetails
import dev.msfjarvis.claw.model.User
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** Simple interface defining an API for lobste.rs */
interface LobstersApi {

  @GET("hottest.json")
  suspend fun getHottestPosts(@Query("page") page: Int): ApiResult<List<LobstersPost>, Unit>

  @GET("newest.json")
  suspend fun getNewestPosts(@Query("page") page: Int): ApiResult<List<LobstersPost>, Unit>

  @GET("s/{postId}.json")
  suspend fun getPostDetails(@Path("postId") postId: String): ApiResult<LobstersPostDetails, Unit>

  @GET("u/{username}.json")
  suspend fun getUser(@Path("username") username: String): ApiResult<User, Unit>

  @GET("t/{tags}.json")
  suspend fun getPostsByTags(
    @Path("tags") tag: Tags,
    @Query("page") page: Int,
  ): ApiResult<List<LobstersPost>, Unit>

  companion object {
    const val BASE_URL = "https://lobste.rs"
  }
}
