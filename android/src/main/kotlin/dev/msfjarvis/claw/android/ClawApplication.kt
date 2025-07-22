/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.deliveryhero.whetstone.Whetstone
import com.deliveryhero.whetstone.app.ApplicationComponentOwner
import com.deliveryhero.whetstone.app.ContributesAppInjector
import dev.msfjarvis.claw.android.work.SavedPostUpdaterWorker
import dev.msfjarvis.claw.core.injection.AppPlugin
import javax.inject.Inject

@ContributesAppInjector(generateAppComponent = true)
class ClawApplication : Application(), ApplicationComponentOwner {

  override val applicationComponent = GeneratedApplicationComponent.create(this)

  @Inject lateinit var plugins: Set<@JvmSuppressWildcards AppPlugin>

  override fun onCreate() {
    Whetstone.inject(this)
    super.onCreate()
    plugins.forEach { plugin -> plugin.apply(this) }
    val postUpdateWorkRequest =
      OneTimeWorkRequestBuilder<SavedPostUpdaterWorker>()
        .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
        .build()
    WorkManager.getInstance(this)
      .enqueueUniqueWork(
        uniqueWorkName = "updateSavedPosts",
        existingWorkPolicy = ExistingWorkPolicy.KEEP,
        request = postUpdateWorkRequest,
      )
  }

  private companion object {
    private const val POST_REFRESH_PERIOD = 24L // 24 hours
  }
}
