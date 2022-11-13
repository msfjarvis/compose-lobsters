/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.injection

import android.content.Context
import android.net.TrafficStats
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import dev.msfjarvis.claw.core.network.DelegatingSocketFactory
import dev.msfjarvis.claw.core.network.NapierLogger
import dev.msfjarvis.claw.core.network.UserAgentInterceptor
import dev.msfjarvis.claw.injection.scopes.AppScope
import java.net.Socket
import javax.net.SocketFactory
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@Module
@ContributesTo(AppScope::class)
interface OkHttpModule {

  @Binds fun NapierLogger.bindLogger(): HttpLoggingInterceptor.Logger

  @Binds @IntoSet fun UserAgentInterceptor.bindUAInterceptor(): Interceptor

  companion object {
    private const val CACHE_SIZE_MB = 10L * 1024 * 1024
    private const val THREAD_STATS_TAG = 0x000090000

    @Provides
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
    fun provideClient(
      cache: Cache,
      socketFactory: SocketFactory,
      interceptors: Set<@JvmSuppressWildcards Interceptor>,
    ): OkHttpClient {
      return OkHttpClient.Builder()
        .apply {
          cache(cache)
          interceptors.forEach(::addNetworkInterceptor)
          socketFactory(socketFactory)
        }
        .build()
    }

    @Provides
    @IntoSet
    fun provideHttpLoggingInterceptor(logger: HttpLoggingInterceptor.Logger): Interceptor {
      return HttpLoggingInterceptor(logger).setLevel(HttpLoggingInterceptor.Level.HEADERS)
    }
  }
}
