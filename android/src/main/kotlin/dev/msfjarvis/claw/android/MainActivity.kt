/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android

import android.app.Activity
import android.app.assist.AssistContent
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.navigation3.runtime.NavKey
import dev.msfjarvis.claw.android.ui.navigation.Comments
import dev.msfjarvis.claw.android.ui.navigation.Hottest
import dev.msfjarvis.claw.android.ui.navigation.Newest
import dev.msfjarvis.claw.android.ui.navigation.Saved
import dev.msfjarvis.claw.android.ui.screens.LobstersPostsScreen
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.core.injection.InjectedViewModelFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.android.ActivityKey
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@ContributesIntoMap(AppScope::class, binding<Activity>())
@ActivityKey(MainActivity::class)
@Inject
class MainActivity(
  private val uriHandler: UriHandler,
  private val viewModelFactory: InjectedViewModelFactory,
) : ComponentActivity() {

  override val defaultViewModelProviderFactory: ViewModelProvider.Factory
    get() = viewModelFactory

  var webUri: String? = null
  var deepLinkDestination by mutableStateOf<NavKey?>(null)
    private set

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    installSplashScreen()
    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
      // Don't set navigation bar style, the default matches the platform behavior.
    )
    handleIntent(intent)
    setContent {
      LobstersTheme(
        dynamicColor = true,
        providedValues =
          arrayOf(
            LocalUriHandler provides uriHandler,
            LocalMetroViewModelFactory provides viewModelFactory,
          ),
      ) {
        val windowSizeClass = calculateWindowSizeClass(this)
        LobstersPostsScreen(
          uriHandler = uriHandler,
          windowSizeClass = windowSizeClass,
          setWebUri = { url -> webUri = url },
          deepLinkDestination = deepLinkDestination,
          clearDeepLink = { deepLinkDestination = null },
        )
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    handleIntent(intent)
  }

  private fun handleIntent(intent: Intent) {
    val data = intent.data
    if (data != null && data.scheme == BuildConfig.DEEPLINK_SCHEME) {
      when (data.host) {
        "comments" -> {
          val postId = data.pathSegments.firstOrNull()
          if (postId != null) {
            deepLinkDestination = Comments(postId)
          }
        }
        "newest" -> deepLinkDestination = Newest
        "hottest" -> deepLinkDestination = Hottest
        "saved" -> deepLinkDestination = Saved
      }
    }
  }

  override fun onProvideAssistContent(outContent: AssistContent?) {
    super.onProvideAssistContent(outContent)
    val uri = webUri
    if (outContent != null) {
      if (uri != null) {
        outContent.webUri = uri.toUri()
      } else {
        outContent.webUri = null
      }
    }
  }
}
