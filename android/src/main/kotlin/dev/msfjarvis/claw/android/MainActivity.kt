package dev.msfjarvis.claw.android

import android.app.assist.AssistContent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import dev.msfjarvis.claw.android.ui.LobstersApp
import dev.msfjarvis.claw.common.comments.HTMLConverter
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
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
      ) { url -> webUri = url }
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
