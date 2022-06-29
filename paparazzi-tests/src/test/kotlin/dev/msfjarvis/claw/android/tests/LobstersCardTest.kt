package dev.msfjarvis.claw.android.tests

import androidx.compose.material3.MaterialTheme
import dev.msfjarvis.claw.common.posts.LobstersCard
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.database.local.SavedPost
import org.junit.Test

class LobstersCardTest : BasePaparazziTest() {
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
  fun verify() {
    paparazzi.snapshot {
      MaterialTheme(colorScheme = theme.colors) { LobstersCard(post, false, postActions) }
    }
  }
}
