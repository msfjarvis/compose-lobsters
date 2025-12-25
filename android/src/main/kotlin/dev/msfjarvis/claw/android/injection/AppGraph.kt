/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.injection

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkManager
import dev.msfjarvis.claw.android.viewmodel.SavedPostsRepository
import dev.msfjarvis.claw.core.injection.AppPlugin
import dev.msfjarvis.claw.core.injection.InjectedWorkerFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.viewmodel.ViewModelGraph
import kotlin.reflect.KClass

@DependencyGraph(AppScope::class)
interface AppGraph : MetroAppComponentProviders, ViewModelGraph {
  val plugins: Set<AppPlugin>

  val workManager: WorkManager

  @Provides
  fun providesWorkManager(application: Context): WorkManager {
    return WorkManager.getInstance(application)
  }

  @Multibinds
  val workerProviders:
    Map<KClass<out ListenableWorker>, Provider<InjectedWorkerFactory.WorkerInstanceFactory<*>>>

  val workerFactory: InjectedWorkerFactory

  val savedPostsRepository: SavedPostsRepository

  @DependencyGraph.Factory
  fun interface Factory {
    fun create(@Provides context: Context): AppGraph
  }
}
