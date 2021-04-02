package dev.msfjarvis.lobsters.data

import com.squareup.moshi.Moshi
import dev.msfjarvis.lobsters.data.api.LobstersApi
import dev.msfjarvis.lobsters.model.LobstersPost
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

class ApiRepository {
  private val moshi = Moshi.Builder().build()
  private val retrofit =
    Retrofit.Builder()
      .baseUrl(LobstersApi.BASE_URL)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()
  private val api: LobstersApi = retrofit.create()

  suspend fun loadPosts(pageNumber: Int): List<LobstersPost> {
    return api.getHottestPosts(pageNumber)
  }
}
