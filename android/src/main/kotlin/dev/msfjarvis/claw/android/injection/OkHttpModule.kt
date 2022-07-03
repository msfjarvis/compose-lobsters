package dev.msfjarvis.claw.android.injection

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
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
  fun provideClient(
    cache: Cache,
    interceptors: Set<@JvmSuppressWildcards Interceptor>,
  ): OkHttpClient {
    return OkHttpClient.Builder()
      .apply {
        cache(cache)
        interceptors.forEach(::addNetworkInterceptor)
      }
      .build()
  }

  @Provides
  @IntoSet
  fun provideHttpLoggingInterceptor(logger: HttpLoggingInterceptor.Logger): Interceptor {
    return HttpLoggingInterceptor(logger).setLevel(HttpLoggingInterceptor.Level.HEADERS)
  }
}
