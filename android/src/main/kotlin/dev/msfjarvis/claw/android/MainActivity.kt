package dev.msfjarvis.claw.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import dev.msfjarvis.claw.common.posts.LobstersItem
import dev.msfjarvis.claw.common.posts.TEST_POST
import dev.msfjarvis.claw.common.theme.LobstersTheme

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      LobstersTheme(darkTheme = isSystemInDarkTheme()) {
        LobstersItem(
          post = TEST_POST,
          isSaved = false,
          {},
          {},
          {},
        )
      }
    }
  }
}
