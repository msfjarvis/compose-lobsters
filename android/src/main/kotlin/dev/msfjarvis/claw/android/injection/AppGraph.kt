/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.injection

import android.content.Context
import androidx.work.WorkManager
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import dev.msfjarvis.claw.android.BuildConfig
import dev.msfjarvis.claw.android.reminders.DailySavedPostReminderScheduler
import dev.msfjarvis.claw.android.viewmodel.CachedRemotePostsRepository
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.api.LobstersParserClient
import dev.msfjarvis.claw.api.ParserRuntimeConfig
import dev.msfjarvis.claw.core.coroutines.MainDispatcher
import dev.msfjarvis.claw.core.work.api.WorkManagerRequests
import dev.msfjarvis.claw.core.work.impl.InjectedWorkerFactory
import dev.msfjarvis.claw.feature.posts.api.SavedPosts
import dev.msfjarvis.claw.feature.tags.api.TagBlocks
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.viewmodel.ViewModelGraph
import kotlin.time.Clock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.datetime.TimeZone

@DependencyGraph(AppScope::class)
interface AppGraph : MetroAppComponentProviders, ViewModelGraph {
  val plugins: Set<AppPlugin>

  val workManagerRequests: Set<WorkManagerRequests>

  val workManager: WorkManager

  val dailySavedPostReminderScheduler: DailySavedPostReminderScheduler

  @Provides
  @SingleIn(AppScope::class)
  fun providesWorkManager(application: Context): WorkManager {
    return WorkManager.getInstance(application)
  }

  @Provides
  @SingleIn(AppScope::class)
  fun providesAppUpdateManager(context: Context): AppUpdateManager =
    AppUpdateManagerFactory.create(context)

  @Provides fun providesClock(): Clock = Clock.System

  @Provides fun providesZoneId(): TimeZone = TimeZone.currentSystemDefault()

  @Provides
  fun providesParserRuntimeConfig(): ParserRuntimeConfig =
    ParserRuntimeConfig(
      manifestUrl = BuildConfig.ZIPLINE_PARSER_MANIFEST_URL,
      verifySignatures = BuildConfig.ZIPLINE_PARSER_VERIFY_SIGNATURES,
    )

  val workerFactory: InjectedWorkerFactory

  val savedPostsRepository: SavedPosts

  val cachedRemotePostsRepository: CachedRemotePostsRepository

  val lobstersApi: LobstersApi

  val tagBlockRepository: TagBlocks

  val ziplineClient: LobstersParserClient

  @MainDispatcher val mainDispatcher: CoroutineDispatcher

  @DependencyGraph.Factory
  fun interface Factory {
    fun create(@Provides context: Context): AppGraph
  }
}
