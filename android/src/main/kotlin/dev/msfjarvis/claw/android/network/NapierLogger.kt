package dev.msfjarvis.claw.android.network

import io.github.aakira.napier.Napier
import javax.inject.Inject
import okhttp3.logging.HttpLoggingInterceptor

/** Implementation of [HttpLoggingInterceptor.Logger] backed by [Napier]. */
class NapierLogger @Inject constructor() : HttpLoggingInterceptor.Logger {
  override fun log(message: String) {
    Napier.d(tag = "LobstersApi") { message }
  }
}
