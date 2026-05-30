/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.parser

import dev.msfjarvis.claw.model.CSRFToken
import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.model.LobstersPostDetails
import dev.msfjarvis.claw.model.ReplyForm
import dev.msfjarvis.claw.model.Tag
import dev.msfjarvis.claw.model.User
import dev.msfjarvis.claw.parser.internal.parseCsrfToken as parseCsrfTokenHtml
import dev.msfjarvis.claw.parser.internal.parsePostDetails as parsePostDetailsHtml
import dev.msfjarvis.claw.parser.internal.parsePostsPage as parsePostsPageHtml
import dev.msfjarvis.claw.parser.internal.parseReplyForm as parseReplyFormHtml
import dev.msfjarvis.claw.parser.internal.parseSearchResults as parseSearchResultsHtml
import dev.msfjarvis.claw.parser.internal.parseTagsPage as parseTagsPageHtml
import dev.msfjarvis.claw.parser.internal.parseUser as parseUserHtml

class LobstersParserServiceImpl : LobstersParserService {
  override fun parsePostsPage(html: String): List<LobstersPost> = parsePostsPageHtml(html)

  override fun parsePostDetails(html: String): LobstersPostDetails = parsePostDetailsHtml(html)

  override fun parseUser(html: String): User = parseUserHtml(html)

  override fun parseTagsPage(html: String): List<Tag> = parseTagsPageHtml(html)

  override fun parseSearchResults(html: String): List<LobstersPost> = parseSearchResultsHtml(html)

  override fun parseCsrfToken(html: String): CSRFToken = parseCsrfTokenHtml(html)

  override fun parseReplyForm(html: String): ReplyForm = parseReplyFormHtml(html)
}
