package dev.msfjarvis.claw.android.injection

import android.content.Context
import android.net.TrafficStats
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dev.msfjarvis.claw.android.network.DelegatingSocketFactory
import java.net.Socket
import javax.net.SocketFactory
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@Module
@InstallIn(SingletonComponent::class)
object OkHttpModule {
  @Provides
  fun provideCache(@ApplicationContext context: Context): Cache {
    return Cache(context.cacheDir, 10 * 1024 * 1024)
  }

  @Provides
  fun provideSocketFactory(): SocketFactory {
    return object : DelegatingSocketFactory(getDefault()) {
      override fun configureSocket(socket: Socket): Socket {
        TrafficStats.setThreadStatsTag(0x000090000)
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
