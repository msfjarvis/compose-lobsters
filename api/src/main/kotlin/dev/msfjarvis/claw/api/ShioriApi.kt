/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api

import com.slack.eithernet.ApiResult
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
  @POST("/api/login") suspend fun login(@Body body: AuthRequest): ApiResult<AuthResponse, Unit>

  @HTTP(method = "POST", path = "/api/logout", hasBody = false)
  suspend fun logout(@Header(SESSION_ID_HEADER) sessionId: String): ApiResult<Unit, Unit>

  @GET("/api/bookmarks")
  suspend fun getBookmarks(
    @Header(SESSION_ID_HEADER) sessionId: String,
  ): ApiResult<BookmarksResponse, Unit>

  @POST("/api/bookmarks")
  suspend fun addBookmark(
    @Header(SESSION_ID_HEADER) sessionId: String,
    @Body bookmarkRequest: BookmarkRequest,
  ): ApiResult<Bookmark, Unit>

  @PUT("/api/bookmarks")
  suspend fun editBookmark(
    @Header(SESSION_ID_HEADER) sessionId: String,
    @Body bookmark: EditedBookmark,
  ): ApiResult<Bookmark, Unit>

  @HTTP(method = "DELETE", path = "/api/bookmarks", hasBody = true)
  suspend fun deleteBookmark(
    @Header(SESSION_ID_HEADER) sessionId: String,
    @Body ids: List<Int>,
  ): ApiResult<Int, Unit>
}
