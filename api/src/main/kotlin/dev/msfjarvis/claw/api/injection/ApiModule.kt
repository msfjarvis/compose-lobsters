/*
 * Copyright © 2023 Harsh Shandilya.
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
import dagger.multibindings.IntKey
import dagger.multibindings.IntoMap
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.api.converters.CSRFTokenConverter
import javax.inject.Qualifier
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit

/**
 * Ideally the multibindings used here would only use [dagger.multibindings.IntoSet], but its lack
 * of ordering guarantees means that we roll a die on each app launch that [Converter]s and
 * [CallAdapter]s are in the correct order to be able to deserialize responses. Thus, the module
 * uses [IntoMap] with [IntKey]s to fake the presence of a fixed order by sorting on the key of the
 * injected [Map]s when injecting them into [Retrofit].
 */
@Module
@ContributesTo(ApplicationScope::class)
object ApiModule {
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

  @Provides @LobstersBaseUrl fun provideBaseUrl(): String = LobstersApi.BASE_URL
}

@Qualifier @Retention(AnnotationRetention.RUNTIME) annotation class LobstersBaseUrl
