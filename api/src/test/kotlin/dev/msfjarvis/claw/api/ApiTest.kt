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
    assertThat(firstPost.shortId).isEqualTo("0mam5k")
    assertThat(firstPost.title).isEqualTo("Lobsters Interview with mitchellh")
    assertThat(firstPost.submitter).isEqualTo("veqq")
    assertThat(firstPost.commentCount).isEqualTo(5)
    assertThat(firstPost.commentsUrl)
      .isEqualTo("https://lobste.rs/s/0mam5k/lobsters_interview_with_mitchellh")
    assertThat(firstPost.tags).containsExactly("interview", "person")
    assertThat(firstPost.userIsAuthor).isTrue()
    assertThat(firstPost.createdAt).isEqualTo("2026-07-09T15:41:15Z")
    Instant.parse(firstPost.createdAt)

    val secondPost = posts.value[1]
    assertThat(secondPost.shortId).isEqualTo("tedi5h")
    assertThat(secondPost.title)
      .isEqualTo(
        "You paid me, a long-time Linux user, to use Windows 11 exclusively for a month: here’s how it went"
      )
    assertThat(secondPost.submitter).isEqualTo("ninakali")
    assertThat(secondPost.commentCount).isEqualTo(19)
    assertThat(secondPost.commentsUrl)
      .isEqualTo("https://lobste.rs/s/tedi5h/you_paid_me_long_time_linux_user_use")
    assertThat(secondPost.tags).containsExactly("windows")
    assertThat(secondPost.userIsAuthor).isFalse()

    val thirdPost = posts.value[2]
    assertThat(thirdPost.shortId).isEqualTo("3eo2nv")
    assertThat(thirdPost.title)
      .isEqualTo("I Did Not Kill Stanley Lieber: How to draw (with 9front)")
    assertThat(thirdPost.submitter).isEqualTo("pmjv")
    assertThat(thirdPost.commentCount).isEqualTo(6)
    assertThat(thirdPost.commentsUrl)
      .isEqualTo("https://lobste.rs/s/3eo2nv/i_did_not_kill_stanley_lieber_how_draw_with")
    assertThat(thirdPost.tags).containsExactly("art")
    assertThat(thirdPost.userIsAuthor).isTrue()
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
    val postDetails = api.getPostDetails("tdfoqh")
    assertIs<Success<LobstersPostDetails>>(postDetails)
    assertThat(postDetails.value.tags).containsExactly("meta")
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
