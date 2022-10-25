/*
 * Copyright © 2021-2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.injection

import com.chimbori.crux.Crux
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object MetadataExtractorModule {
  @Provides
  fun provideCrux(
    okHttpClient: OkHttpClient,
  ): Crux {
    return Crux(okHttpClient = okHttpClient)
  }
}
