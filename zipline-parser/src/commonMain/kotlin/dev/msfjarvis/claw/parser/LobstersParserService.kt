/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.parser

import app.cash.zipline.ZiplineService
import dev.msfjarvis.claw.model.CSRFToken
import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.model.LobstersPostDetails
import dev.msfjarvis.claw.model.ReplyForm
import dev.msfjarvis.claw.model.Tag
import dev.msfjarvis.claw.model.User

interface LobstersParserService : ZiplineService {
  fun parsePostsPage(html: String): List<LobstersPost>

  fun parsePostDetails(html: String): LobstersPostDetails

  fun parseUser(html: String): User

  fun parseTagsPage(html: String): List<Tag>

  fun parseSearchResults(html: String): List<LobstersPost>

  fun parseCsrfToken(html: String): CSRFToken

  fun parseReplyForm(html: String): ReplyForm
}
