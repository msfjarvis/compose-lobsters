package dev.msfjarvis.claw.android

import android.app.assist.AssistContent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import dev.msfjarvis.claw.android.ui.LobstersApp
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  @Inject lateinit var urlLauncher: UrlLauncher
  private var webUri: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    installSplashScreen()
    WindowCompat.setDecorFitsSystemWindows(window, false)
    setContent {
      LobstersApp(
        urlLauncher = urlLauncher,
      ) { url -> webUri = url }
    }
  }

  override fun onProvideAssistContent(outContent: AssistContent?) {
    super.onProvideAssistContent(outContent)
    webUri?.let { outContent?.webUri = Uri.parse(it) }
  }
}
