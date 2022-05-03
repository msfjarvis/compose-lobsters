package dev.msfjarvis.claw.android.injection

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.msfjarvis.claw.api.LobstersApi
import io.github.aakira.napier.Napier
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create

@OptIn(ExperimentalSerializationApi::class)
@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

  @Provides
  fun provideCache(@ApplicationContext context: Context): Cache {
    return Cache(context.cacheDir, 10 * 1024 * 1024)
  }

  @Provides
  fun provideClient(cache: Lazy<Cache>): OkHttpClient {
    return OkHttpClient.Builder()
      .cache(cache.get())
      .addNetworkInterceptor { chain ->
        val request = chain.request()
        Napier.d(tag = "LobstersApi") { "${request.method}: ${request.url}" }
        chain.proceed(request)
      }
      .build()
  }

  /**
   * Using [Lazy] here is a trick I picked up from Zac Sweers, which he explained in more detail
   * here: https://www.zacsweers.dev/dagger-party-tricks-deferred-okhttp-init/
   */
  @Provides
  fun provideRetrofit(
    client: Lazy<OkHttpClient>,
    json: Lazy<Json>,
  ): Retrofit {
    val contentType = "application/json".toMediaType()
    return Retrofit.Builder()
      .client(client.get())
      .baseUrl(LobstersApi.BASE_URL)
      .addConverterFactory(json.get().asConverterFactory(contentType))
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
