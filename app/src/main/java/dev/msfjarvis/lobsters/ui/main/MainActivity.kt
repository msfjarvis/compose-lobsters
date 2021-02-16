package dev.msfjarvis.lobsters.ui.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Providers
import dagger.hilt.android.AndroidEntryPoint
import dev.msfjarvis.lobsters.ui.theme.LobstersTheme
import dev.msfjarvis.lobsters.ui.urllauncher.LocalUrlLauncher
import dev.msfjarvis.lobsters.ui.urllauncher.UrlLauncher
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  @Inject lateinit var urlLauncher: UrlLauncher

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      Providers(LocalUrlLauncher provides urlLauncher) {
        LobstersTheme {
          LobstersApp()
        }
      }
    }
  }
}
