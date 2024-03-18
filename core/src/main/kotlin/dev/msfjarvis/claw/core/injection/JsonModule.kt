/*
 * Copyright © 2021-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.injection

import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntKey
import dagger.multibindings.IntoMap
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Converter
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Module
@ContributesTo(ApplicationScope::class)
@OptIn(ExperimentalSerializationApi::class)
object JsonModule {

  @Provides
  @IntKey(1)
  @IntoMap
  fun provideJsonConverterFactory(json: Json): Converter.Factory {
    val contentType = "application/json".toMediaType()
    return json.asConverterFactory(contentType)
  }

  @Provides
  fun provideJsonSerializer(): Json {
    return Json {
      ignoreUnknownKeys = true
      namingStrategy = JsonNamingStrategy.SnakeCase
    }
  }
}
