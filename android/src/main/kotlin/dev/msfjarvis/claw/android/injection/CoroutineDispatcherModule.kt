package dev.msfjarvis.claw.android.injection

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.msfjarvis.claw.util.coroutines.DefaultDispatcherProvider
import dev.msfjarvis.claw.util.coroutines.DispatcherProvider
import javax.inject.Qualifier
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
abstract class CoroutineDispatcherModule {

  @Binds abstract fun DefaultDispatcherProvider.bind(): DispatcherProvider

  companion object {
    @[Provides IODispatcher]
    fun provideIODispatcher(dispatcherProvider: DispatcherProvider): CoroutineDispatcher {
      return dispatcherProvider.io()
    }

    @[Provides DatabaseDispatcher]
    fun provideDatabaseDispatcher(dispatcherProvider: DispatcherProvider): CoroutineDispatcher {
      return dispatcherProvider.database()
    }

    @[Provides MainDispatcher]
    fun provideMainDispatcher(dispatcherProvider: DispatcherProvider): CoroutineDispatcher {
      return dispatcherProvider.main()
    }
  }
}

@Qualifier @Retention(AnnotationRetention.RUNTIME) annotation class DatabaseDispatcher

@Qualifier @Retention(AnnotationRetention.RUNTIME) annotation class MainDispatcher

@Qualifier @Retention(AnnotationRetention.RUNTIME) annotation class IODispatcher
