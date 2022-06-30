package dev.msfjarvis.claw.android.interceptors

import dev.msfjarvis.claw.android.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class UserAgentInterceptor : Interceptor {
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
