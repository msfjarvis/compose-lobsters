/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RealLobstersHtmlParserJvmTest {
  @Test
  fun parsesPostsPage() {
    val html = checkNotNull(javaClass.classLoader.getResource("hottest_page.html")).readText()
    val service = LobstersParserServiceImpl()

    val posts = service.parsePostsPage(html)

    assertTrue(posts.isNotEmpty())
  }

  @Test
  fun parsesSearchResults() {
    val html =
      checkNotNull(javaClass.classLoader.getResource("search_chatgpt_page.html")).readText()
    val service = LobstersParserServiceImpl()

    assertTrue(service.parseSearchResults(html).isNotEmpty())
  }

  @Test
  fun searchResultsUseAbsoluteStoryUrls() {
    val html =
      """
      <div class="story_liner h-entry">
        <span class="link h-cite"><a href="/s/abcd12/local_story">Local story</a></span>
        <span class="comments_label"><a href="/s/abcd12/local_story">1 comment</a></span>
        <span class="tags"><a>meta</a></span>
        <div class="byline"><a href="/~someone">someone</a></div>
      </div>
      """
        .trimIndent()
    val service = LobstersParserServiceImpl()

    val result = service.parseSearchResults(html).single()

    assertEquals("https://lobste.rs/s/abcd12/local_story", result.url)
  }

  @Test
  fun parsesUserPage() {
    val html = checkNotNull(javaClass.classLoader.getResource("msfjarvis.html")).readText()
    val service = LobstersParserServiceImpl()

    assertTrue(service.parseUser(html).username.isNotBlank())
  }

  @Test
  fun parsesTagsPage() {
    val html = checkNotNull(javaClass.classLoader.getResource("tags.html")).readText()
    val service = LobstersParserServiceImpl()

    assertTrue(service.parseTagsPage(html).isNotEmpty())
  }

  @Test
  fun parsesCsrfToken() {
    val html = checkNotNull(javaClass.classLoader.getResource("csrf_page.html")).readText()
    val service = LobstersParserServiceImpl()

    assertTrue(service.parseCsrfToken(html).value.isNotBlank())
  }

  @Test
  fun parsesReplyForm() {
    val html = checkNotNull(javaClass.classLoader.getResource("reply_form.html")).readText()
    val service = LobstersParserServiceImpl()

    assertTrue(service.parseReplyForm(html).authenticityToken.isNotBlank())
  }

  @Test
  fun parsesPostDetails() {
    val html =
      checkNotNull(javaClass.classLoader.getResource("post_details_tdfoqh.html")).readText()
    val service = LobstersParserServiceImpl()

    val details = service.parsePostDetails(html)

    assertTrue(details.title.isNotBlank())
    assertTrue(details.comments.isNotEmpty())
  }

  @Test
  fun commentsWithoutVisibleUpvoterCountHaveOnePointFromTheAuthor() {
    val html =
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
    val service = LobstersParserServiceImpl()

    val details = service.parsePostDetails(html)

    kotlin.test.assertEquals(1, details.comments.single().score)
  }
}
