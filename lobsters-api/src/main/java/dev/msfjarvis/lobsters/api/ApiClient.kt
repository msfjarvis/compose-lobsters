package dev.msfjarvis.lobsters.api

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiClient {
  inline fun <reified T> getClient(baseUrl: String): T {
    return Retrofit.Builder()
      .baseUrl(baseUrl)
      .addConverterFactory(MoshiConverterFactory.create())
      .build()
      .create(T::class.java)
  }
}
