/*
 * Copyright © 2021-2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.injection

import com.deliveryhero.whetstone.app.ApplicationScope
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Converter

@Module
@ContributesTo(ApplicationScope::class)
object RetrofitModule {

  @OptIn(ExperimentalSerializationApi::class)
  @Provides
  @IntoSet
  fun provideJsonConverterFactory(json: Json): Converter.Factory {
    val contentType = MediaType.get("application/json")
    return json.asConverterFactory(contentType)
  }

  @Provides
  fun provideJsonSerializer(): Json {
    return Json { ignoreUnknownKeys = true }
  }
}
