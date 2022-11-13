/*
 * Copyright © 2021-2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.injection

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.slack.eithernet.ApiResultCallAdapterFactory
import com.slack.eithernet.ApiResultConverterFactory
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.injection.scopes.AppScope
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create

@OptIn(ExperimentalSerializationApi::class)
@Module
@ContributesTo(AppScope::class)
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
