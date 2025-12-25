/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.network

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntKey
import dev.zacsweers.metro.IntoMap
import dev.zacsweers.metro.Provides
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Converter
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@BindingContainer
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
