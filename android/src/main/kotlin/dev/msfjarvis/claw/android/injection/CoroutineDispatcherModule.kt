/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.injection

import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import dagger.Provides
import dev.msfjarvis.claw.injection.scopes.AppScope
import dev.msfjarvis.claw.util.coroutines.DefaultDispatcherProvider
import dev.msfjarvis.claw.util.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher

@Module
@ContributesTo(AppScope::class)
interface CoroutineDispatcherModule {

  @Binds fun DefaultDispatcherProvider.bind(): DispatcherProvider

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
