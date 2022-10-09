package dev.msfjarvis.claw.android.injection

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.slack.eithernet.ApiResultCallAdapterFactory
import com.slack.eithernet.ApiResultConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.msfjarvis.claw.api.LobstersApi
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create

@OptIn(ExperimentalSerializationApi::class)
@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
  @Provides
  fun provideRetrofit(
    client: OkHttpClient,
    json: Json,
  ): Retrofit {
    val contentType = "application/json".toMediaType()
    return Retrofit.Builder()
      .client(client)
      .baseUrl(LobstersApi.BASE_URL)
      .addConverterFactory(ApiResultConverterFactory)
      .addConverterFactory(json.asConverterFactory(contentType))
      .addCallAdapterFactory(ApiResultCallAdapterFactory)
      .build()
  }

  @Provides
  fun provideApi(retrofit: Retrofit): LobstersApi {
    return retrofit.create()
  }

  @Provides
  fun provideJsonSerializer(): Json {
    return Json { ignoreUnknownKeys = true }
  }
}
