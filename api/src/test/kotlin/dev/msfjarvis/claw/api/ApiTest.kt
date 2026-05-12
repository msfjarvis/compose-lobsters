/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api

import com.fleeksoft.ksoup.Ksoup
import com.google.common.truth.Truth.assertThat
import com.slack.eithernet.ApiResult.Success
import com.slack.eithernet.test.newEitherNetController
import dev.burnoo.kspoon.Kspoon
import dev.msfjarvis.claw.model.LobstersPostDetails
import dev.msfjarvis.claw.model.User
import dev.msfjarvis.claw.util.TestUtils.assertIs
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class ApiTest {
  private val wrapper = ApiWrapper(newEitherNetController())
  private val api
    get() = wrapper.api

  @Test
  fun `api gets correct number of items`() = runTest {
    val posts = api.getHottestPosts(1)
    assertIs<Success<PostsPage>>(posts)
    assertThat(posts.value.posts).hasSize(25)
  }

  @Test
  fun `posts with no urls`() = runTest {
    val posts = api.getHottestPosts(1)
    assertIs<Success<PostsPage>>(posts)
    val commentsOnlyPosts = posts.value.posts.asSequence().filter { it.url.isEmpty() }.toSet()
    assertThat(commentsOnlyPosts).hasSize(0)
  }

  @Test
  fun `api parses hottest HTML fixture fields`() = runTest {
    val posts = api.getHottestPosts(1)
    assertIs<Success<PostsPage>>(posts)

    val firstPost = posts.value.posts[0]
    assertThat(firstPost.shortId).isEqualTo("ho7nqt")
    assertThat(firstPost.title).isEqualTo("On forking the Web")
    assertThat(firstPost.submitter).isEqualTo("spc476")
    assertThat(firstPost.commentCount).isEqualTo(33)
    assertThat(firstPost.commentsUrl).isEqualTo("https://lobste.rs/s/ho7nqt/on_forking_web")
    assertThat(firstPost.tags).containsExactly("web")
    assertThat(firstPost.userIsAuthor).isFalse()
    assertThat(firstPost.createdAt).isEqualTo("2026-05-09T10:03:17Z")
    DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(firstPost.createdAt)

    val secondPost = posts.value.posts[1]
    assertThat(secondPost.shortId).isEqualTo("vbit2a")
    assertThat(secondPost.title).isEqualTo("I Will Not Add Query Strings to Your URLs")
    assertThat(secondPost.submitter).isEqualTo("susam")
    assertThat(secondPost.commentCount).isEqualTo(2)
    assertThat(secondPost.commentsUrl)
      .isEqualTo("https://lobste.rs/s/vbit2a/i_will_not_add_query_strings_your_urls")
    assertThat(secondPost.tags).containsExactly("web")
    assertThat(secondPost.userIsAuthor).isTrue()

    val noCommentsPost = posts.value.posts.first { it.shortId == "sjbrlg" }
    assertThat(noCommentsPost.title).isEqualTo("Yggdrasil Network as an Embedded Go Library")
    assertThat(noCommentsPost.submitter).isEqualTo("asciimoth")
    assertThat(noCommentsPost.commentCount).isEqualTo(0)
    assertThat(noCommentsPost.commentsUrl)
      .isEqualTo("https://lobste.rs/s/sjbrlg/yggdrasil_network_as_embedded_go_library")
    assertThat(noCommentsPost.tags)
      .containsExactly("distributed", "go", "networking", "programming")
    assertThat(noCommentsPost.userIsAuthor).isTrue()
  }

  @Test
  fun `api gets newest posts`() = runTest {
    val posts = api.getNewestPosts(1)
    assertIs<Success<PostsPage>>(posts)
    assertThat(posts.value.posts).hasSize(25)
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
    val kspoon = Kspoon {
      parse = { html -> Ksoup.parse(html, baseUri = LobstersApi.BASE_URL) }
      coerceInputValues = true
    }

    val postDetails =
      kspoon.parse<LobstersPostDetails>(
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
        "RPe1m3JaXAq0ti7t15hNixpLvmgJ793Ti6y4fOpxtfenyXQK3WU19m7MJb8RGoIf-tC3C9Ilwb8pv0WJUWLbNA"
      )
  }

  @Test
  fun `retrieve tags`() = runTest {
    val tags = api.getTags()
    assertIs<Success<TagsPage>>(tags)
    assertThat(tags.value.tags).isNotEmpty()
    val rubyTag = tags.value.tags.first { it.tag == "ruby" }
    assertThat(rubyTag.description).isEqualTo("Ruby programming")
    assertThat(rubyTag.privileged).isFalse()
    assertThat(rubyTag.active).isTrue()
    assertThat(rubyTag.category).isEmpty()
    assertThat(rubyTag.isMedia).isFalse()
    assertThat(rubyTag.hotnessMod).isEqualTo(0.0)

    val newsTag = tags.value.tags.first { it.tag == "news" }
    assertThat(newsTag.active).isFalse()

    val videoTag = tags.value.tags.first { it.tag == "video" }
    assertThat(videoTag.isMedia).isTrue()
  }
}
