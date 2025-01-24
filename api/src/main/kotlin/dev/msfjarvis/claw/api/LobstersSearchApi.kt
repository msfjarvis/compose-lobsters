/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api

import com.slack.eithernet.ApiResult
import dev.msfjarvis.claw.model.LobstersPost
import retrofit2.http.GET
import retrofit2.http.Query

interface LobstersSearchApi {
  @GET("/search?what=stories&order=newest")
  suspend fun searchPosts(
    @Query("q") searchQuery: String,
    @Query("page") page: Int,
  ): ApiResult<List<LobstersPost>, Unit>
}
