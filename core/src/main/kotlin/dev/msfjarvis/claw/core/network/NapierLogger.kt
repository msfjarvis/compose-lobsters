/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.network

import io.github.aakira.napier.Napier
import javax.inject.Inject
import okhttp3.logging.HttpLoggingInterceptor

/** Implementation of [HttpLoggingInterceptor.Logger] backed by [Napier]. */
class NapierLogger @Inject constructor() : HttpLoggingInterceptor.Logger {
  override fun log(message: String) {
    Napier.d(tag = "LobstersApi") { message }
  }
}
