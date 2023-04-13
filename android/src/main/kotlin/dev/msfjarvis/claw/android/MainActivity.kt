/*
 * Copyright © 2021-2023 Harsh Shandilya.
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
import androidx.core.view.WindowCompat
import com.deliveryhero.whetstone.Whetstone
import com.deliveryhero.whetstone.activity.ContributesActivityInjector
import dev.msfjarvis.claw.android.ui.LobstersApp
import dev.msfjarvis.claw.common.comments.HTMLConverter
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import javax.inject.Inject

@ContributesActivityInjector
class MainActivity : ComponentActivity() {

  @Inject lateinit var urlLauncher: UrlLauncher
  @Inject lateinit var htmlConverter: HTMLConverter
  private var webUri: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    installSplashScreen()
    Whetstone.inject(this)
    WindowCompat.setDecorFitsSystemWindows(window, false)
    setContent {
      LobstersApp(
        urlLauncher = urlLauncher,
        htmlConverter = htmlConverter,
        setWebUri = { url -> webUri = url },
      )
    }
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
}
