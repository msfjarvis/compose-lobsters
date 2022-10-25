/*
 * Copyright © 2021-2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android

import android.app.assist.AssistContent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import dev.msfjarvis.claw.android.ui.LobstersApp
import dev.msfjarvis.claw.android.work.SavedPostUpdaterWorker
import dev.msfjarvis.claw.common.comments.HTMLConverter
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  @Inject lateinit var urlLauncher: UrlLauncher
  @Inject lateinit var htmlConverter: HTMLConverter
  private var webUri: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    installSplashScreen()
    setContent {
      LobstersApp(
        urlLauncher = urlLauncher,
        htmlConverter = htmlConverter,
        setWebUri = { url -> webUri = url },
      )
    }
    val postUpdateWorkRequest =
      PeriodicWorkRequestBuilder<SavedPostUpdaterWorker>(POST_REFRESH_PERIOD, TimeUnit.HOURS)
        .setConstraints(
          Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresDeviceIdle(true)
            .build()
        )
        .build()
    WorkManager.getInstance(this@MainActivity)
      .enqueueUniquePeriodicWork(
        "updateSavedPosts",
        ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
        postUpdateWorkRequest,
      )
  }

  override fun onProvideAssistContent(outContent: AssistContent?) {
    super.onProvideAssistContent(outContent)
    if (outContent != null) {
      if (webUri != null) {
        outContent.webUri = Uri.parse(webUri)
      } else {
        outContent.webUri = null
      }
    }
  }

  private companion object {
    private const val POST_REFRESH_PERIOD = 24L // 24 hours
  }
}
