/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api

import com.slack.eithernet.ApiResult
import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.model.LobstersPostDetails
import dev.msfjarvis.claw.model.Tag
import dev.msfjarvis.claw.model.User
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
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

  @GET("tags.json") suspend fun getTags(): ApiResult<List<Tag>, Unit>

  @Multipart
  @POST("comments/{commentId}/upvote")
  suspend fun upvoteComment(
    @Path("commentId") commentId: String,
    @Header("x-csrf-token") csrfToken: String,
    @Header("x-requested-with") requestedWith: String,
    @Part reason: MultipartBody.Part,
  ): ApiResult<Unit, Unit>

  @Multipart
  @POST("comments/{commentId}/unvote")
  suspend fun unvoteComment(
    @Path("commentId") commentId: String,
    @Header("x-csrf-token") csrfToken: String,
    @Header("x-requested-with") requestedWith: String,
    @Part reason: MultipartBody.Part,
  ): ApiResult<Unit, Unit>

  @GET("comments/{commentId}/reply")
  suspend fun getReplyForm(
    @Path("commentId") commentId: String,
    @Header("x-csrf-token") csrfToken: String,
    @Header("x-requested-with") requestedWith: String,
  ): ApiResult<ReplyForm, Unit>

  @Multipart
  @POST("comments")
  suspend fun postReply(
    @Header("x-csrf-token") csrfToken: String,
    @Header("x-requested-with") requestedWith: String,
    @Part authenticityToken: MultipartBody.Part,
    @Part storyId: MultipartBody.Part,
    @Part method: MultipartBody.Part,
    @Part parentCommentShortId: MultipartBody.Part,
    @Part comment: MultipartBody.Part,
    @Part commit: MultipartBody.Part,
  ): ApiResult<Unit, Unit>

  companion object {
    const val BASE_URL = "https://lobste.rs"
  }
}
