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
    assertEquals(listOf("interview", "person"), posts.first().tags)
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
    val story = details.stories.first()

    assertTrue(story.title.isNotBlank())
    assertTrue(story.description.isNotBlank())
    assertEquals(
      "https://lobste.rs/s/tdfoqh/ranking_comments_by_sum_replies_scores",
      details.commentsUrl,
    )
    assertEquals(listOf("meta"), story.tags)
    assertTrue(story.userIsAuthor)
    assertTrue(details.comments.isNotEmpty())
  }

  @Test
  fun commentSubmitterDoesNotMarkTheSourceStoryAsAuthored() {
    val html =
      checkNotNull(javaClass.classLoader.getResource("post_details_ktew3s.html")).readText()
    val service = LobstersParserServiceImpl()

    val details = service.parsePostDetails(html)

    assertEquals(false, details.stories.first().userIsAuthor)
  }

  @Test
  fun parsesPostDetailsWithSubmitterComment() {
    val html =
      checkNotNull(javaClass.classLoader.getResource("post_details_d7m5j1.html")).readText()
    val service = LobstersParserServiceImpl()

    val details = service.parsePostDetails(html)
    val story = details.stories.first()

    assertTrue(story.userIsAuthor)
    assertTrue(details.comments.any { it.user == story.submitter })
  }

  @Test
  fun parsesMergedSourcesAndAttributesCommentsToTheirStory() {
    val html =
      """
      <ol class="stories">
        <li class="story" data-shortid="primary1">
          <div class="details" id="header_story_primary"><span class="link h-cite"><a href="/s/primary1/primary">Primary</a></span><ul class="tags"><li><a>main</a></li></ul><div class="byline"><a href="/~alice">alice</a><time data-at-unix="1710000000"></time></div></div>
          <div class="details" id="header_story_secondary"><span class="link h-cite"><a href="/s/secondary2/secondary">Secondary</a></span><ul class="tags"><li><a>other</a></li></ul><div class="byline"><a href="/~bob">bob</a><time data-at-unix="1710000001"></time></div></div>
        </li>
      </ol>
      <div class="details" id="comments_story_primary"><a href="#comments-primary1">0 comments</a></div>
      <div class="details" id="comments_story_secondary"><a href="#comments-secondary2">1 comment</a></div>
      <li class="comments_subtree" id="comments-primary1"><ol class="comments" id="story_comments"></ol></li>
      <li class="comments_subtree" id="comments-secondary2"><ol class="comments" id="story_comments">
        <li class="comments_subtree"><div class="comment" data-shortid="comment1"><div class="byline"><a href="/~carol">carol</a><a href="/c/comment1"><time data-at-unix="1710000002"></time></a></div><div class="comment_text">Hello</div></div></li>
      </ol></li>
      """
        .trimIndent()

    val details = LobstersParserServiceImpl().parsePostDetails(html)

    assertEquals(listOf("primary1", "secondary2"), details.stories.map { it.shortId })
    assertEquals(listOf("main"), details.stories.first().tags)
    assertEquals(listOf("other"), details.stories.last().tags)
    assertEquals("secondary2", details.comments.single().sourceStoryId)
    assertEquals("comment1", details.comments.single().shortId)
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

  @Test
  fun commentScoresUseUpvoterTitleWhenVisibleTextIsOmitted() {
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
    val service = LobstersParserServiceImpl()

    val details = service.parsePostDetails(html)

    kotlin.test.assertEquals(12, details.comments.single().score)
  }
}
