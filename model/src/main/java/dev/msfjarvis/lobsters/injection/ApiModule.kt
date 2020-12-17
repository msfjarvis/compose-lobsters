package dev.msfjarvis.lobsters.injection

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Lazy
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dev.msfjarvis.lobsters.data.api.LobstersApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

@Module
@InstallIn(ActivityComponent::class)
object ApiModule {
  @Provides
  fun provideClient(): OkHttpClient {
    return OkHttpClient.Builder()
      .build()
  }

  /**
   * Using [Lazy] here is a trick I picked up from Zac Sweers, which he explained in more
   * detail here: https://www.zacsweers.dev/dagger-party-tricks-deferred-okhttp-init/
   */
  @Provides
  fun provideRetrofit(
    client: Lazy<OkHttpClient>,
    moshi: Lazy<Moshi>,
  ): Retrofit {
    return Retrofit.Builder()
      .client(client.get())
      .baseUrl(LobstersApi.BASE_URL)
      .addConverterFactory(MoshiConverterFactory.create(moshi.get()))
      .build()
  }

  @Provides
  fun provideApi(retrofit: Retrofit): LobstersApi {
    return retrofit.create()
  }
}
