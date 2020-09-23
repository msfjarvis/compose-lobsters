package dev.msfjarvis.lobsters.api

import dev.msfjarvis.lobsters.model.LobstersPost
import retrofit2.http.GET
import retrofit2.http.Query

interface LobstersApi {
  @GET("hottest.json")
  suspend fun getHottestPosts(@Query("page") page: Int): List<LobstersPost>
}
