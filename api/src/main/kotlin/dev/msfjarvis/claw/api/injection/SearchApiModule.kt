/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api.injection

import com.deliveryhero.whetstone.app.ApplicationScope
import com.slack.eithernet.ApiResultConverterFactory
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dev.msfjarvis.claw.api.LobstersSearchApi
import dev.msfjarvis.claw.api.converters.SearchConverter
import javax.inject.Qualifier
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.create

@Module
@ContributesTo(ApplicationScope::class)
class SearchApiModule {

  @Qualifier @Retention(AnnotationRetention.RUNTIME) private annotation class SearchApi

  @Provides
  @SearchApi
  fun provideConverters(): List<@JvmSuppressWildcards Converter.Factory> =
    listOf(
      ApiResultConverterFactory,
      SearchConverter.Factory,
    )

  @Provides
  @SearchApi
  fun provideSearchApiRetrofit(
    client: OkHttpClient,
    @SearchApi converterFactories: List<@JvmSuppressWildcards Converter.Factory>,
    callAdapterFactories: Map<Int, @JvmSuppressWildcards CallAdapter.Factory>,
    @LobstersBaseUrl baseUrl: String,
  ): Retrofit {
    return Retrofit.Builder()
      .client(client)
      .baseUrl(baseUrl)
      .apply { converterFactories.forEach(this::addConverterFactory) }
      .apply { callAdapterFactories.toSortedMap().values.forEach(this::addCallAdapterFactory) }
      .addConverterFactory(SearchConverter.Factory)
      .build()
  }

  @Provides
  fun provideSearchApi(@SearchApi retrofit: Retrofit): LobstersSearchApi = retrofit.create()
}
