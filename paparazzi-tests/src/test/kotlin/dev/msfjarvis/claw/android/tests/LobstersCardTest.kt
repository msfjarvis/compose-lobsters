package dev.msfjarvis.claw.android.tests

import androidx.compose.material3.MaterialTheme
import app.cash.paparazzi.Paparazzi
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import dev.msfjarvis.claw.common.posts.LobstersCard
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.database.local.SavedPost
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class LobstersCardTest {
  @get:Rule val paparazzi = Paparazzi()
  companion object {
    private val post =
      SavedPost(
        shortId = "shortId",
        title = "Title",
        url = "/s/shortId",
        createdAt = "2021-04-03T16:16:02.000-05:00",
        commentCount = 10,
        commentsUrl = "/s/shortId",
        submitterName = "msfjarvis",
        submitterAvatarUrl = "/msfjarvis.png",
        tags = listOf("science", "technology"),
      )
    private val postActions =
      object : PostActions {
        override fun viewPost(postUrl: String, commentsUrl: String) {}

        override fun viewComments(postId: String) {}

        override fun viewCommentsPage(commentsUrl: String) {}

        override fun toggleSave(post: SavedPost) {}
      }
  }

  @Test
  fun verify(@TestParameter theme: Theme) {
    paparazzi.snapshot {
      MaterialTheme(colorScheme = theme.colors) { LobstersCard(post, false, postActions) }
    }
  }
}
