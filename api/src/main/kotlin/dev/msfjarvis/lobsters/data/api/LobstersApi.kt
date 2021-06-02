package dev.msfjarvis.lobsters.data.api

import dev.msfjarvis.lobsters.model.LobstersPost
import dev.msfjarvis.lobsters.model.LobstersPostDetails
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** Simple interface defining an API for lobste.rs */
interface LobstersApi {

  @GET("hottest.json") suspend fun getHottestPosts(@Query("page") page: Int): List<LobstersPost>

  @GET("newest.json") suspend fun getNewestPosts(@Query("page") page: Int): List<LobstersPost>

  @GET("s/{postId}.json")
  suspend fun getPostDetails(@Path("postId") postId: String): LobstersPostDetails

  companion object {
    const val BASE_URL = "https://lobste.rs"
  }
}
