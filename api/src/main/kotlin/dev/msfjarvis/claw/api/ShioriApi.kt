/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api

import android.annotation.SuppressLint
import dev.msfjarvis.claw.model.shiori.AuthRequest
import dev.msfjarvis.claw.model.shiori.AuthResponse
import dev.msfjarvis.claw.model.shiori.Bookmark
import dev.msfjarvis.claw.model.shiori.BookmarkRequest
import dev.msfjarvis.claw.model.shiori.BookmarksResponse
import dev.msfjarvis.claw.model.shiori.EditedBookmark
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

private const val SESSION_ID_HEADER = "X-Session-Id"

interface ShioriApi {
  @POST("/api/login") suspend fun login(@Body body: AuthRequest): AuthResponse

  @SuppressLint("RetrofitUsage") // POST without a body is apparently fine?
  @POST("/api/logout")
  suspend fun logout(@Header(SESSION_ID_HEADER) sessionId: String)

  @GET("/api/bookmarks")
  suspend fun getBookmarks(@Header(SESSION_ID_HEADER) sessionId: String): BookmarksResponse

  @POST("/api/bookmarks")
  suspend fun addBookmark(
    @Header(SESSION_ID_HEADER) sessionId: String,
    @Body bookmarkRequest: BookmarkRequest,
  ): Bookmark

  @PUT("/api/bookmarks")
  suspend fun editBookmark(
    @Header(SESSION_ID_HEADER) sessionId: String,
    @Body bookmark: EditedBookmark,
  ): Bookmark

  @HTTP(method = "DELETE", path = "/api/bookmarks", hasBody = true)
  suspend fun deleteBookmark(
    @Header(SESSION_ID_HEADER) sessionId: String,
    @Body ids: List<Int>,
  ): Int
}