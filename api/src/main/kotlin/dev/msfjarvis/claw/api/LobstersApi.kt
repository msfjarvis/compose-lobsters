package dev.msfjarvis.claw.api

import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.model.LobstersPostDetails
import dev.msfjarvis.claw.model.User
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** Simple interface defining an API for lobste.rs */
interface LobstersApi {

  @GET("hottest.json") suspend fun getHottestPosts(@Query("page") page: Int): List<LobstersPost>

  @GET("newest.json") suspend fun getNewestPosts(@Query("page") page: Int): List<LobstersPost>

  @GET("s/{postId}.json")
  suspend fun getPostDetails(@Path("postId") postId: String): LobstersPostDetails

  @GET("u/{username}.json") suspend fun getUser(@Path("username") username: String): User

  companion object {
    const val BASE_URL = "https://lobste.rs"
  }
}
