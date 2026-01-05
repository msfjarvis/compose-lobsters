/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.network

import android.util.Log
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import okhttp3.logging.HttpLoggingInterceptor

/** Implementation of [HttpLoggingInterceptor.Logger] backed by [Log]. */
@ContributesBinding(AppScope::class)
@Inject
class OkHttpLogger : HttpLoggingInterceptor.Logger {
  override fun log(message: String) {
    Log.d("LobstersApi", message)
  }
}
