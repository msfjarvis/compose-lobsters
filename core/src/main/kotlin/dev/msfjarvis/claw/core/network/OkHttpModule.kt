/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.network

import android.content.Context
import android.net.TrafficStats
import dev.zacsweers.metro.ForScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Module
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.multibindings.IntoSet
import java.net.Socket
import javax.net.SocketFactory
import okhttp3.Cache
import okhttp3.CertificatePinner
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@Module
@ContributesTo(AppScope::class)
interface OkHttpModule {

  @Binds fun bindLogger(impl: NapierLogger): HttpLoggingInterceptor.Logger

  @Binds @IntoSet fun bindUAInterceptor(impl: UserAgentInterceptor): Interceptor

  companion object {
    private const val CACHE_SIZE_MB = 10L * 1024 * 1024
    private const val THREAD_STATS_TAG = 0x000090000

    @Provides
    fun provideCertificatePinner(): CertificatePinner {
      return CertificatePinner.Builder()
        .add("lobste.rs", "sha256/Bla1TIdpGeHXQS0/CIrA5hhFhOTZd94IIJRS3G3AcIo=")
        .add("lobste.rs", "sha256/jQJTbIh0grw0/1TkHSumWb+Fs0Ggogr621gT3PvPKG0=")
        .add("lobste.rs", "sha256/C5+lpZ7tcVwmwQIMcRtPbsQtWLABXhQzejna0wHFr8M=")
        .build()
    }

    @Provides
    fun provideCache(@ForScope(AppScope::class) context: Context): Cache {
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
    fun provideClient(
      cache: Cache,
      socketFactory: SocketFactory,
      interceptors: Set<@JvmSuppressWildcards Interceptor>,
      certificatePinner: CertificatePinner,
    ): OkHttpClient {
      return OkHttpClient.Builder()
        .apply {
          followRedirects(true)
          followSslRedirects(true)
          retryOnConnectionFailure(true)
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
}
