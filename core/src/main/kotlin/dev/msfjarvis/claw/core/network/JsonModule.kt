/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.network

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Module
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.multibindings.IntKey
import dev.zacsweers.metro.multibindings.IntoMap
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Converter
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Module
@ContributesTo(AppScope::class)
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
      namingStrategy = JsonNamingStrategy.Builtins.SnakeCase
    }
  }
}
