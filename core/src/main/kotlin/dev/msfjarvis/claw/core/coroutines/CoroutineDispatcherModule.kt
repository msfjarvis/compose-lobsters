/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.coroutines

import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Qualifier
import kotlinx.coroutines.CoroutineDispatcher

@Qualifier @Retention(AnnotationRetention.RUNTIME) annotation class DatabaseReadDispatcher

@Qualifier @Retention(AnnotationRetention.RUNTIME) annotation class DatabaseWriteDispatcher

@Qualifier @Retention(AnnotationRetention.RUNTIME) annotation class MainDispatcher

@Qualifier @Retention(AnnotationRetention.RUNTIME) annotation class IODispatcher

@Qualifier @Retention(AnnotationRetention.RUNTIME) annotation class DefaultDispatcher

@Module
@ContributesTo(ApplicationScope::class)
interface CoroutineDispatcherModule {

  @Binds fun bindDispatcherProvider(impl: DefaultDispatcherProvider): DispatcherProvider

  companion object {
    @[Provides IODispatcher]
    fun provideIODispatcher(dispatcherProvider: DispatcherProvider): CoroutineDispatcher {
      return dispatcherProvider.io()
    }

    @[Provides DatabaseReadDispatcher]
    fun provideDatabaseReadDispatcher(dispatcherProvider: DispatcherProvider): CoroutineDispatcher {
      return dispatcherProvider.databaseRead()
    }

    @[Provides DatabaseWriteDispatcher]
    fun provideDatabaseWriteDispatcher(
      dispatcherProvider: DispatcherProvider
    ): CoroutineDispatcher {
      return dispatcherProvider.databaseWrite()
    }

    @[Provides MainDispatcher]
    fun provideMainDispatcher(dispatcherProvider: DispatcherProvider): CoroutineDispatcher {
      return dispatcherProvider.main()
    }

    @[Provides DefaultDispatcher]
    fun provideDefaultDispatcher(dispatcherProvider: DispatcherProvider): CoroutineDispatcher {
      return dispatcherProvider.default()
    }
  }
}
