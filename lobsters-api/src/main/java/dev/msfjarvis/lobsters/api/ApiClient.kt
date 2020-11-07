package dev.msfjarvis.lobsters.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit

object ApiClient {
  inline fun <reified T> getClient(baseUrl: String): T {
    return Retrofit.Builder()
      .baseUrl(baseUrl)
      .addConverterFactory(Json.asConverterFactory(MediaType.get("application/json")))
      .build()
      .create(T::class.java)
  }
}
