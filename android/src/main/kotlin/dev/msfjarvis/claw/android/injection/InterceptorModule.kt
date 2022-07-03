package dev.msfjarvis.claw.android.injection

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dev.msfjarvis.claw.android.network.NapierLogger
import dev.msfjarvis.claw.android.network.UserAgentInterceptor
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor

@Module
@InstallIn(SingletonComponent::class)
interface InterceptorModule {

  @Binds fun NapierLogger.bindLogger(): HttpLoggingInterceptor.Logger

  @Binds @IntoSet fun UserAgentInterceptor.bindUAInterceptor(): Interceptor
}
