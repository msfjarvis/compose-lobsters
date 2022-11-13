/*
 * Copyright © 2021-2022 Harsh Shandilya.
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
  ): Retrofit {
    return Retrofit.Builder()
      .client(client)
      .baseUrl(LobstersApi.BASE_URL)
      .addConverterFactory(ApiResultConverterFactory)
      .addCallAdapterFactory(ApiResultCallAdapterFactory)
      .apply { converterFactories.forEach(this::addConverterFactory) }
      .build()
  }

  @Provides
  fun provideApi(retrofit: Retrofit): LobstersApi {
    return retrofit.create()
  }
}
