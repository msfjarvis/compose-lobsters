/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api.injection

import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dev.msfjarvis.claw.api.ShioriApi
import javax.inject.Qualifier
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.create

@Module
@ContributesTo(ApplicationScope::class)
class ShioriApiModule {

  @Qualifier @Retention(AnnotationRetention.RUNTIME) private annotation class Shiori

  @Provides
  @Shiori
  fun provideShioriRetrofit(
    client: OkHttpClient,
    converterFactories: Map<Int, @JvmSuppressWildcards Converter.Factory>,
    callAdapterFactories: Map<Int, @JvmSuppressWildcards CallAdapter.Factory>,
  ): Retrofit {
    return Retrofit.Builder()
      .client(client)
      .baseUrl("https://example.com")
      .apply { converterFactories.toSortedMap().values.forEach(this::addConverterFactory) }
      .apply { callAdapterFactories.toSortedMap().values.forEach(this::addCallAdapterFactory) }
      .build()
  }

  @Provides fun provideShioriApi(@Shiori retrofit: Retrofit): ShioriApi = retrofit.create()
}
