/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.network

import android.content.Context
import android.net.TrafficStats
import dev.msfjarvis.claw.core.network.DelegatingSocketFactory
import dev.msfjarvis.claw.core.network.OkHttpClientConfigurator
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import java.net.Socket
import javax.net.SocketFactory
import okhttp3.Cache

@BindingContainer
@ContributesTo(AppScope::class)
object OkHttpAndroidModule {
  private const val CACHE_SIZE_MB = 10L * 1024 * 1024
  private const val THREAD_STATS_TAG = 0x000090000

  @Provides
  @SingleIn(AppScope::class)
  fun provideCache(context: Context): Cache {
    return Cache(context.cacheDir, CACHE_SIZE_MB)
  }

  @Provides
  fun provideSocketFactory(): SocketFactory {
    return object : DelegatingSocketFactory(SocketFactory.getDefault()) {
      override fun configureSocket(socket: Socket): Socket {
        TrafficStats.setThreadStatsTag(THREAD_STATS_TAG)
        return super.configureSocket(socket)
      }
    }
  }

  @Provides
  @IntoSet
  fun provideClientConfigurator(
    cache: Cache,
    socketFactory: SocketFactory,
  ): OkHttpClientConfigurator {
    return OkHttpClientConfigurator { builder ->
      builder.cache(cache)
      builder.socketFactory(socketFactory)
    }
  }
}
