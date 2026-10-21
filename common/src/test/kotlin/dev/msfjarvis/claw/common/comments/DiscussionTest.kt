/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments

import com.google.common.truth.Truth.assertThat
import dev.msfjarvis.claw.model.Comment
import dev.msfjarvis.claw.model.Discussion
import dev.msfjarvis.claw.model.SourceStory
import kotlin.time.Instant
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class DiscussionTest {
  @Test
  fun `discussion provides primary story and concatenated tags`() {
    val primary = story("primary", listOf("kotlin", "android"))
    val secondary = story("secondary", listOf("android", "compose"))

    val discussion = Discussion(stories = listOf(primary, secondary))

    assertThat(discussion.first).isEqualTo(primary)
    assertThat(discussion.allTags)
      .containsExactly("kotlin", "android", "android", "compose")
      .inOrder()
  }

  @Test
  fun `discussion rejects an empty story list`() {
    val exception = makeFail { Discussion(stories = emptyList()) }

    assertThat(exception).isInstanceOf(IllegalArgumentException::class.java)
    assertThat(exception)
      .hasMessageThat()
      .isEqualTo("At least one source story must be part of a discussion")
  }

  @Test
  fun `discussion rejects duplicate story IDs`() {
    val exception = makeFail { Discussion(stories = listOf(story("same"), story("same"))) }

    assertThat(exception).isInstanceOf(IllegalArgumentException::class.java)
    assertThat(exception).hasMessageThat().isEqualTo("Source story IDs must be unique")
  }

  @Test
  fun `discussion rejects comments with unknown source IDs`() {
    val exception = makeFail {
      Discussion(stories = listOf(story("primary")), comments = listOf(comment("missing")))
    }

    assertThat(exception).isInstanceOf(IllegalArgumentException::class.java)
    assertThat(exception)
      .hasMessageThat()
      .isEqualTo("Every comment must refer to a source story in the discussion")
  }

  private fun story(shortId: String, tags: List<String> = emptyList()) =
    SourceStory(
      shortId = shortId,
      createdAt = "",
      title = shortId,
      url = "",
      submitter = "",
      tags = tags,
    )

  private fun comment(sourceStoryId: String) =
    Comment(
      shortId = "comment",
      comment = "",
      timestamp = Instant.fromEpochSeconds(0),
      sourceStoryId = sourceStoryId,
    )

  private fun makeFail(block: () -> Unit): Throwable =
    try {
      block()
      fail { "Should throw" }
    } catch (e: Throwable) {
      e
    }
}
