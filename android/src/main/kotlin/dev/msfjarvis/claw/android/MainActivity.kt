/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android

import android.app.assist.AssistContent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.platform.LocalUriHandler
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.deliveryhero.whetstone.Whetstone
import com.deliveryhero.whetstone.activity.ContributesActivityInjector
import dev.msfjarvis.claw.android.ui.screens.LobstersPostsScreen
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import javax.inject.Inject

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@ContributesActivityInjector
class MainActivity : ComponentActivity() {

  @Inject lateinit var urlLauncher: UrlLauncher
  var webUri: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    installSplashScreen()
    Whetstone.inject(this)
    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
      // Don't set navigation bar style, the default matches the platform behavior.
    )
    setContent {
      LobstersTheme(
        dynamicColor = true,
        providedValues = arrayOf(LocalUriHandler provides urlLauncher),
      ) {
        val windowSizeClass = calculateWindowSizeClass(this)
        LobstersPostsScreen(
          urlLauncher = urlLauncher,
          windowSizeClass = windowSizeClass,
          setWebUri = { url -> webUri = url },
        )
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

  companion object {
    const val NAVIGATION_KEY = "postId"
  }
}
