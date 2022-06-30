package dev.msfjarvis.claw.android.interceptors

import io.github.aakira.napier.Napier
import okhttp3.Interceptor
import okhttp3.Response

class NapierLoggingInterceptor : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()
    Napier.d(tag = "LobstersApi") { "${request.method}: ${request.url}" }
    return chain.proceed(request)
  }
}
