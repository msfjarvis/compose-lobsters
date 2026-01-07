/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.network

import android.content.Context
import android.net.TrafficStats
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import java.net.Socket
import java.util.concurrent.TimeUnit
import javax.net.SocketFactory
import okhttp3.Cache
import okhttp3.CertificatePinner
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@BindingContainer
@ContributesTo(AppScope::class)
object OkHttpModule {
  private const val CACHE_SIZE_MB = 10L * 1024 * 1024
  private const val THREAD_STATS_TAG = 0x000090000
  private const val CONNECT_TIMEOUT_SECONDS = 30L
  private const val READ_TIMEOUT_SECONDS = 30L
  private const val WRITE_TIMEOUT_SECONDS = 30L

  @Provides
  fun provideCertificatePinner(): CertificatePinner {
    return CertificatePinner.Builder()
      .add("lobste.rs", "sha256/Bla1TIdpGeHXQS0/CIrA5hhFhOTZd94IIJRS3G3AcIo=")
      .add("lobste.rs", "sha256/jQJTbIh0grw0/1TkHSumWb+Fs0Ggogr621gT3PvPKG0=")
      .add("lobste.rs", "sha256/C5+lpZ7tcVwmwQIMcRtPbsQtWLABXhQzejna0wHFr8M=")
      .build()
  }

  @Provides
  @SingleIn(AppScope::class)
  fun provideCache(context: Context): Cache {
    return Cache(context.cacheDir, CACHE_SIZE_MB)
  }

  @Provides
  fun provideSocketFactory(): SocketFactory {
    return object : DelegatingSocketFactory(getDefault()) {
      override fun configureSocket(socket: Socket): Socket {
        TrafficStats.setThreadStatsTag(THREAD_STATS_TAG)
        return super.configureSocket(socket)
      }
    }
  }

  @Provides
  @SingleIn(AppScope::class)
  fun provideClient(
    cache: Cache,
    socketFactory: SocketFactory,
    interceptors: Set<Interceptor>,
    certificatePinner: CertificatePinner,
  ): OkHttpClient {
    return OkHttpClient.Builder()
      .apply {
        followRedirects(true)
        followSslRedirects(true)
        retryOnConnectionFailure(true)
        connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        cache(cache)
        interceptors.forEach(::addNetworkInterceptor)
        socketFactory(socketFactory)
        certificatePinner(certificatePinner)
      }
      .build()
  }

  @Provides
  @IntoSet
  fun provideHttpLoggingInterceptor(logger: HttpLoggingInterceptor.Logger): Interceptor {
    return HttpLoggingInterceptor(logger).setLevel(HttpLoggingInterceptor.Level.BASIC)
  }
}
