package dev.msfjarvis.claw.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import dev.msfjarvis.claw.android.theme.LobstersTheme
import dev.msfjarvis.claw.common.posts.LobstersItem
import dev.msfjarvis.lobsters.data.local.SavedPost

val TEST_POST =
  SavedPost(
    shortId = "zqyydb",
    title = "k2k20 hackathon report: Bob Beck on LibreSSL progress",
    url = "https://undeadly.org/cgi?action=article;sid=20200921105847",
    createdAt = "2020-09-21T07:11:14.000-05:00",
    commentsUrl = "https://lobste.rs/s/zqyydb/k2k20_hackathon_report_bob_beck_on",
    submitterName = "Vigdis",
    submitterAvatarUrl = "/404.html",
    tags = listOf("openbsd", "linux", "containers", "hack the planet", "no thanks"),
  )

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      LobstersTheme {
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
