/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.network

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.aakira.napier.Napier
import okhttp3.logging.HttpLoggingInterceptor

/** Implementation of [HttpLoggingInterceptor.Logger] backed by [Napier]. */
@ContributesBinding(AppScope::class)
@Inject
class NapierLogger : HttpLoggingInterceptor.Logger {
  override fun log(message: String) {
    Napier.d(tag = "LobstersApi") { message }
  }
}
