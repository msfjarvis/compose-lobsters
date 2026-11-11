/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.posts

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.model.Discussion
import dev.msfjarvis.claw.model.SourceStory
import dev.msfjarvis.claw.model.UIPost

@PreviewTest
@Preview(name = "Comment count above 99", widthDp = 400, showBackground = true)
@Composable
private fun LobstersCardWithOverflowingCommentCountScreenshot() {
  LobstersTheme {
    LobstersCard(
      post =
        UIPost(
          discussion =
            Discussion(
              stories =
                listOf(
                  SourceStory(
                    shortId = "bdzmde",
                    title =
                      "I'm brave enough to say it: Linux is good now, and if you want to feel like you actually own your PC, make 2026 the year of Linux on (your) desktop",
                    url =
                      "https://www.pcgamer.com/software/linux/im-brave-enough-to-say-it-linux-is-good-now-and-if-you-want-to-feel-like-you-actually-own-your-pc-make-2026-the-year-of-linux-on-your-desktop",
                    createdAt = "2026-01-02T09:49:33.000-06:00",
                    submitter = "rau",
                    tags = listOf("linux", "games", "vibecoding", "concatenative"),
                  )
                )
            ),
          commentCount = 300,
          commentsUrl = "https://lobste.rs/s/q1hh1g/simple_anomaly_detection_using_plain_sql",
          description = "",
        ),
      isSaved = false,
      isRead = false,
      onPostClick = {},
      onCommentsClick = {},
      onSaveClick = {},
    )
  }
}
