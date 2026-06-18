/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.network

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import java.util.concurrent.TimeUnit
import okhttp3.CookieJar
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@BindingContainer
@ContributesTo(AppScope::class)
object OkHttpModule {
  private const val CONNECT_TIMEOUT_SECONDS = 30L
  private const val READ_TIMEOUT_SECONDS = 30L
  private const val WRITE_TIMEOUT_SECONDS = 30L

  @Provides
  @SingleIn(AppScope::class)
  fun provideClient(
    interceptors: Set<Interceptor>,
    cookieJar: CookieJar,
    configurators: Set<OkHttpClientConfigurator>,
  ): OkHttpClient {
    return OkHttpClient.Builder()
      .apply {
        followRedirects(true)
        followSslRedirects(true)
        retryOnConnectionFailure(true)
        connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        cookieJar(cookieJar)
        // Use application interceptors instead of network interceptors to allow retrying
        // requests. Network interceptors must call proceed() exactly once, but
        // RetryAfterInterceptor needs to call proceed() twice when retrying after a delay.
        interceptors.forEach(::addInterceptor)
        configurators.forEach { configurator -> configurator.configure(this) }
      }
      .build()
  }

  @Provides
  @IntoSet
  fun provideHttpLoggingInterceptor(logger: HttpLoggingInterceptor.Logger): Interceptor {
    return HttpLoggingInterceptor(logger).setLevel(HttpLoggingInterceptor.Level.BASIC)
  }
}
