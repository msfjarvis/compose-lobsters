/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api.injection

import com.slack.eithernet.integration.retrofit.ApiResultCallAdapterFactory
import com.slack.eithernet.integration.retrofit.ApiResultConverterFactory
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.api.LobstersSearchApi
import dev.msfjarvis.claw.api.converters.CSRFTokenConverter
import dev.msfjarvis.claw.api.converters.SearchConverter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Module
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.Qualifier
import dev.zacsweers.metro.multibindings.IntKey
import dev.zacsweers.metro.multibindings.IntoMap
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.create

/**
 * Ideally the multibindings used here would only use [dev.zacsweers.metro.multibindings.IntoSet],
 * but its lack of ordering guarantees means that we roll a die on each app launch that [Converter]s
 * and [CallAdapter]s are in the correct order to be able to deserialize responses. Thus, the module
 * uses [IntoMap] with [IntKey]s to fake the presence of a fixed order by sorting on the key of the
 * injected [Map]s when injecting them into [Retrofit].
 */
@Module
@ContributesTo(AppScope::class)
object RetrofitModule {
  @Provides
  fun provideRetrofit(
    client: OkHttpClient,
    converterFactories: Map<Int, @JvmSuppressWildcards Converter.Factory>,
    callAdapterFactories: Map<Int, @JvmSuppressWildcards CallAdapter.Factory>,
    @Named("LobstersURL") baseUrl: String,
  ): Retrofit {
    return Retrofit.Builder()
      .client(client)
      .baseUrl(baseUrl)
      .apply { converterFactories.toSortedMap().values.forEach(this::addConverterFactory) }
      .apply { callAdapterFactories.toSortedMap().values.forEach(this::addCallAdapterFactory) }
      .build()
  }

  @Provides
  @SearchApi
  fun provideSearchApiRetrofit(
    client: OkHttpClient,
    @SearchApi converterFactories: List<@JvmSuppressWildcards Converter.Factory>,
    callAdapterFactories: Map<Int, @JvmSuppressWildcards CallAdapter.Factory>,
    @Named("LobstersURL") baseUrl: String,
  ): Retrofit {
    return Retrofit.Builder()
      .client(client)
      .baseUrl(baseUrl)
      .apply { converterFactories.forEach(this::addConverterFactory) }
      .apply { callAdapterFactories.toSortedMap().values.forEach(this::addCallAdapterFactory) }
      .addConverterFactory(SearchConverter.Factory)
      .build()
  }

  @Provides fun provideApi(retrofit: Retrofit): LobstersApi = retrofit.create()

  @Provides
  fun provideSearchApi(@SearchApi retrofit: Retrofit): LobstersSearchApi = retrofit.create()

  @Provides
  @IntKey(0)
  @IntoMap
  fun provideApiResultConverter(): Converter.Factory = ApiResultConverterFactory

  @Provides
  @IntKey(0)
  @IntoMap
  fun provideApiResultCallAdapter(): CallAdapter.Factory = ApiResultCallAdapterFactory

  @Provides
  @IntKey(Int.MAX_VALUE)
  @IntoMap
  fun provideCSRFTokenConverter(): Converter.Factory = CSRFTokenConverter.Factory

  @Provides
  @SearchApi
  fun provideConverters(): List<@JvmSuppressWildcards Converter.Factory> =
    listOf(ApiResultConverterFactory, SearchConverter.Factory)

  @Provides @Named("LobstersURL") fun provideLobstersUrl(): String = LobstersApi.BASE_URL
}

/** Internal Dagger [Qualifier] to identify dependencies exclusive to [LobstersSearchApi]. */
@Qualifier @Retention(AnnotationRetention.RUNTIME) private annotation class SearchApi
