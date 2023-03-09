/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api.injection

import com.deliveryhero.whetstone.app.ApplicationScope
import com.slack.eithernet.ApiResultCallAdapterFactory
import com.slack.eithernet.ApiResultConverterFactory
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dev.msfjarvis.claw.api.LobstersApi
import javax.inject.Qualifier
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.create

@Module
@ContributesTo(ApplicationScope::class)
object ApiModule {
  @Provides
  fun provideRetrofit(
    client: OkHttpClient,
    converterFactories: Set<@JvmSuppressWildcards Converter.Factory>,
    @BaseUrl baseUrl: String,
  ): Retrofit {
    return Retrofit.Builder()
      .client(client)
      .baseUrl(baseUrl)
      .addConverterFactory(ApiResultConverterFactory)
      .addCallAdapterFactory(ApiResultCallAdapterFactory)
      .apply { converterFactories.forEach(this::addConverterFactory) }
      .build()
  }

  @Provides
  fun provideApi(retrofit: Retrofit): LobstersApi {
    return retrofit.create()
  }

  @Provides @BaseUrl fun provideBaseUrl(): String = LobstersApi.BASE_URL
}

@Qualifier @Retention(AnnotationRetention.RUNTIME) annotation class BaseUrl
