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

    val firstPost = posts.value[0]
    assertThat(firstPost.shortId).isEqualTo("jp3nva")
    assertThat(firstPost.title).isEqualTo("You probably don't need Yocto, and that's fine")
    assertThat(firstPost.submitter).isEqualTo("rw-rw-rw-")
    assertThat(firstPost.commentCount).isEqualTo(9)
    assertThat(firstPost.commentsUrl)
      .isEqualTo("https://lobste.rs/s/jp3nva/you_probably_don_t_need_yocto_s_fine")
    assertThat(firstPost.tags).containsExactly("linux")
    assertThat(firstPost.userIsAuthor).isTrue()
    assertThat(firstPost.createdAt).isEqualTo("2026-05-29T09:08:12Z")
    Instant.parse(firstPost.createdAt)

    val secondPost = posts.value[1]
    assertThat(secondPost.shortId).isEqualTo("lc26ar")
    assertThat(secondPost.title).isEqualTo("SQLite Does Not Accept Agentic Code")
    assertThat(secondPost.submitter).isEqualTo("hoistbypetard")
    assertThat(secondPost.commentCount).isEqualTo(15)
    assertThat(secondPost.commentsUrl)
      .isEqualTo("https://lobste.rs/s/lc26ar/sqlite_does_not_accept_agentic_code")
    assertThat(secondPost.tags).containsExactly("vibecoding")
    assertThat(secondPost.userIsAuthor).isFalse()

    val noCommentsPost = posts.value.first { it.shortId == "1fkt8w" }
    assertThat(noCommentsPost.title).isEqualTo("Patching my guitar amp's firmware")
    assertThat(noCommentsPost.submitter).isEqualTo("mcf")
    assertThat(noCommentsPost.commentCount).isEqualTo(0)
    assertThat(noCommentsPost.commentsUrl)
      .isEqualTo("https://lobste.rs/s/1fkt8w/patching_my_guitar_amp_s_firmware")
    assertThat(noCommentsPost.tags).containsExactly("hardware", "reversing")
    assertThat(noCommentsPost.userIsAuthor).isTrue()
  }

  @Test
  fun `api gets newest posts`() = runTest {
    val posts = api.getNewestPosts(1)
    assertIs<Success<List<LobstersPost>>>(posts)
    assertThat(posts.value).hasSize(25)
  }

  @Test
  fun `post details with comments`() = runTest {
    val postDetails = api.getPostDetails("tdfoqh")
    assertIs<Success<LobstersPostDetails>>(postDetails)
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
  fun `edited comments expose a single timestamp and edited state`() = runTest {
    val postDetails = api.getPostDetails("tdfoqh")
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

    val newsTag = tags.value.first { it.tag == "news" }
    assertThat(newsTag.active).isFalse()

    val videoTag = tags.value.first { it.tag == "video" }
    assertThat(videoTag.isMedia).isTrue()
  }
}
