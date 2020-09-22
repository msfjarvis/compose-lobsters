package dev.msfjarvis.lobsters.api

import dev.msfjarvis.lobsters.model.LobstersPost
import retrofit2.Call
import retrofit2.http.GET

interface LobstersApi {
  @GET("hottest.json")
  fun getHottestPosts(): Call<List<dev.msfjarvis.lobsters.model.LobstersPost>>
}
