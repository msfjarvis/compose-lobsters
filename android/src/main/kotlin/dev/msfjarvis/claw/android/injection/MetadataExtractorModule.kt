/*
 * Copyright © 2021-2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.injection

import com.chimbori.crux.Crux
import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient

@Module
@ContributesTo(ApplicationScope::class)
object MetadataExtractorModule {
  @Provides
  fun provideCrux(
    okHttpClient: OkHttpClient,
  ): Crux {
    return Crux(okHttpClient = okHttpClient)
  }
}
