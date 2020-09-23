package dev.msfjarvis.lobsters.api

import dev.msfjarvis.lobsters.model.LobstersPost
import retrofit2.http.GET

interface LobstersApi {
  @GET("hottest.json")
  suspend fun getHottestPosts(): List<LobstersPost>
}
