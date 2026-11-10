/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.lists

import dev.msfjarvis.claw.model.Discussion
import dev.msfjarvis.claw.model.SourceStory
import dev.msfjarvis.claw.model.UIPost

internal fun previewPosts(count: Int): List<UIPost> =
  List(count) { index ->
    val shortId = "post$index"
    UIPost(
      discussion =
        Discussion(
          stories =
            listOf(
              SourceStory(
                shortId = shortId,
                createdAt = "2026-01-01T00:00:00Z",
                title = "Example post $index",
                url = "https://example.com/$shortId",
                submitter = "author",
                tags = listOf("example"),
              )
            )
        ),
      description = "Example description",
      commentCount = index,
      commentsUrl = "https://lobste.rs/s/$shortId/c",
    )
  }
