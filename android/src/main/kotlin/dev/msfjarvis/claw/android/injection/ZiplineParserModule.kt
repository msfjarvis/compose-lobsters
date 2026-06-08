/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.injection

import android.content.Context
import dev.msfjarvis.claw.android.BuildConfig
import dev.msfjarvis.claw.android.zipline.AndroidZiplineParserClient
import dev.msfjarvis.claw.api.LobstersParserClient
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import okhttp3.OkHttpClient

@BindingContainer
@ContributesTo(AppScope::class)
object ZiplineParserModule {
  @Provides
  @Named("LobstersParserManifestUrl")
  fun provideLobstersParserManifestUrl(): String = BuildConfig.ZIPLINE_PARSER_MANIFEST_URL

  @Provides
  @SingleIn(AppScope::class)
  fun provideParserClient(
    context: Context,
    client: OkHttpClient,
    @Named("LobstersParserManifestUrl") manifestUrl: String,
  ): LobstersParserClient =
    AndroidZiplineParserClient(
      context = context,
      manifestUrl = manifestUrl,
      httpClient = client,
      verifySignatures = BuildConfig.ZIPLINE_PARSER_VERIFY_SIGNATURES,
    )
}
