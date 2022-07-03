package dev.msfjarvis.claw.android.network

import dev.msfjarvis.claw.android.BuildConfig
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

/** An OkHttp [Interceptor] that adds a recognizable User-Agent header to all network requests. */
class UserAgentInterceptor @Inject constructor() : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    return chain.proceed(
      chain
        .request()
        .newBuilder()
        .header("User-Agent", "Claw-Android/${BuildConfig.VERSION_NAME}/msfjarvis")
        .build()
    )
  }
}
