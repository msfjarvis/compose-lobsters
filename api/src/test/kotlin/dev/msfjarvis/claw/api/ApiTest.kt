/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api

import com.google.common.truth.Truth.assertThat
import com.slack.eithernet.ApiResult.Success
import dev.msfjarvis.claw.model.CSRFToken
import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.model.LobstersPostDetails
import dev.msfjarvis.claw.model.Tag
import dev.msfjarvis.claw.model.User
import dev.msfjarvis.claw.parser.LobstersParserServiceImpl
import dev.msfjarvis.claw.util.TestUtils.assertIs
import kotlin.time.Instant
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class ApiTest {
  private val wrapper = ApiWrapper()
  private val api
    get() = wrapper.api

  @Test
  fun `api gets correct number of items`() = runTest {
    val posts = api.getHottestPosts(1)
    assertIs<Success<List<LobstersPost>>>(posts)
    assertThat(posts.value).hasSize(25)
  }

  @Test
  fun `posts with no urls`() = runTest {
    val posts = api.getHottestPosts(1)
    assertIs<Success<List<LobstersPost>>>(posts)
    val commentsOnlyPosts = posts.value.asSequence().filter { it.url.isEmpty() }.toSet()
    assertThat(commentsOnlyPosts).hasSize(0)
  }

  @Test
  fun `api parses hottest HTML fixture fields`() = runTest {
    val posts = api.getHottestPosts(1)
    assertIs<Success<List<LobstersPost>>>(posts)

    val firstPost = posts.value.first()
    assertThat(firstPost.shortId).isEqualTo("drwd4j")
    assertThat(firstPost.title).isEqualTo("Jepsen: ParticleDB 1.1.4")
    assertThat(firstPost.url).isEqualTo("https://jepsen.io/analyses/particledb-1.1.4")
    assertThat(firstPost.submitter).isEqualTo("aphyr")
    assertThat(firstPost.commentCount).isEqualTo(17)
    assertThat(firstPost.commentsUrl)
      .isEqualTo("https://lobste.rs/s/drwd4j/jepsen_particledb_1_1_4")
    assertThat(firstPost.tags).containsExactly("databases", "testing")
    assertThat(firstPost.userIsAuthor).isTrue()
    assertThat(firstPost.createdAt).isEqualTo("2026-10-31T13:39:52Z")
    Instant.parse(firstPost.createdAt)
  }

  @Test
  fun `api gets newest posts`() = runTest {
    val posts = api.getNewestPosts(1)
    assertIs<Success<List<LobstersPost>>>(posts)
    assertThat(posts.value).hasSize(25)
    assertThat(posts.value.first().tags).isNotEmpty()
  }

  @Test
  fun `post details with comments`() = runTest {
    val postDetails = api.getPostDetails("https://lobste.rs/s/tdfoqh/c")
    assertIs<Success<LobstersPostDetails>>(postDetails)
    assertThat(postDetails.value.stories.first().tags).containsExactly("meta")
    val comments = postDetails.value.comments
    assertThat(comments).hasSize(10)
    assertThat(comments.first().user).isEqualTo("dpercy")
    assertThat(comments.first { it.shortId == "pcvbcd" }.score).isEqualTo(2)
    assertThat(comments.first().comment).contains("Maybe take the max, instead of the sum?")
    assertThat(comments.first { it.shortId == "pcvbcd" }.parentComment).isEqualTo("m3wyu5")
    assertThat(comments.first { it.shortId == "lqqn3a" }.parentComment).isEqualTo("owddle")
  }

  @Test
  fun `comments without visible upvoter count have one point from the author`() {
    val parser = LobstersParserServiceImpl()

    val postDetails =
      parser.parsePostDetails(
        """
        <ol class="stories">
          <li class="story" data-shortid="story1">
            <span class="link h-cite"><a href="/s/story1/test">Test story</a></span>
            <div class="byline">
              <a class="u-author" href="/~/submitter">submitter</a>
              <time data-at-unix="1710000000"></time>
            </div>
          </li>
        </ol>
        <ol class="comments">
          <li class="comments_subtree">
            <div class="comment" data-shortid="abc123">
              <div class="voters"></div>
              <div class="details">
                <div class="byline">
                  <a href="/~/author">author</a>
                  <a href="/c/abc123"><time data-at-unix="1710000000"></time></a>
                </div>
                <div class="comment_text"><p>Hello</p></div>
              </div>
            </div>
          </li>
        </ol>
        """
          .trimIndent()
      )

    assertThat(postDetails.comments.single().score).isEqualTo(1)
  }

  @Test
  fun `comments use upvoter title when the visible score is omitted`() {
    val parser = LobstersParserServiceImpl()

    val postDetails =
      parser.parsePostDetails(
        """
        <ol class="stories">
          <li class="story" data-shortid="story1">
            <span class="link h-cite"><a href="/s/story1/test">Test story</a></span>
            <div class="byline">
              <a class="u-author" href="/~/submitter">submitter</a>
              <time data-at-unix="1710000000"></time>
            </div>
          </li>
        </ol>
        <ol class="comments">
          <li class="comments_subtree">
            <div class="comment" data-shortid="abc123">
              <div class="voters">
                <label for="comment_folder_abc123" class="comment_folder"></label>
                <a class="upvoter" title="12" href="/login"></a>
              </div>
              <div class="details">
                <div class="byline">
                  <a href="/~/author">author</a>
                  <a href="/c/abc123"><time data-at-unix="1710000000"></time></a>
                </div>
                <div class="comment_text"><p>Hello</p></div>
              </div>
            </div>
          </li>
        </ol>
        """
          .trimIndent()
      )

    assertThat(postDetails.comments.single().score).isEqualTo(12)
  }

  @Test
  fun `edited comments expose a single timestamp and edited state`() = runTest {
    val postDetails = api.getPostDetails("https://lobste.rs/s/tdfoqh/c")
    assertIs<Success<LobstersPostDetails>>(postDetails)

    val editedComment = postDetails.value.comments.first { it.shortId == "pcvbcd" }
    assertThat(editedComment.edited).isTrue()
    assertThat(editedComment.timestamp.epochSeconds).isEqualTo(1658588955)
  }

  @Test
  fun `post details preserves upvoted comments`() = runTest {
    val postDetails = wrapper.upvotedPostDetails
    assertThat(postDetails.comments.filter { it.isUpvoted }.map { it.shortId })
      .containsExactly("ncdsfc")
  }

  @Test
  fun `get user details`() = runTest {
    val user = api.getUser("msfjarvis")
    assertIs<Success<User>>(user)
    assertThat(user.value.username).isEqualTo("msfjarvis")
  }

  @Test
  fun `retrieve CSRF token`() = runTest {
    val token = api.getCSRFToken()
    assertIs<Success<CSRFToken>>(token)
    assertThat(token.value.value)
      .isEqualTo(
        "dvJ8r_CkOImcHQ5ZLUWlJeQVoPEPQ3rK85DNgiZJcehafqwYP8jESW8AhMf0uQGLqqLbsarYiISCghnDaUd6wA"
      )
  }

  @Test
  fun `retrieve tags`() = runTest {
    val tags = api.getTags()
    assertIs<Success<List<Tag>>>(tags)
    assertThat(tags.value).isNotEmpty()
    val rubyTag = tags.value.first { it.tag == "ruby" }
    assertThat(rubyTag.description).isEqualTo("Ruby programming")
    assertThat(rubyTag.privileged).isFalse()
    assertThat(rubyTag.active).isTrue()
    assertThat(rubyTag.category).isEmpty()
    assertThat(rubyTag.isMedia).isFalse()
    assertThat(rubyTag.hotnessMod).isEqualTo(0.0)

    val videoTag = tags.value.first { it.tag == "video" }
    assertThat(videoTag.isMedia).isTrue()

    val transcriptTag = tags.value.first { it.tag == "transcript" }
    assertThat(transcriptTag.isMedia).isTrue()
  }
}
