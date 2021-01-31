package dev.msfjarvis.lobsters.data.api

import dev.msfjarvis.lobsters.data.local.LobstersPost
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Simple interface defining an API for lobste.rs
 */
interface LobstersApi {

  @GET("hottest.json")
  suspend fun getHottestPosts(@Query("page") page: Int): List<LobstersPost>

  companion object {
    const val BASE_URL = "https://lobste.rs"
  }
}
