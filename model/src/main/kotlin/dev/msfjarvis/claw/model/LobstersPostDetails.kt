/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("LongParameterList")

package dev.msfjarvis.claw.model

import dev.burnoo.kspoon.SelectorHtmlTextMode
import dev.burnoo.kspoon.annotation.Selector
import dev.drewhamilton.poko.Poko
import dev.msfjarvis.claw.database.local.SavedPost
import io.mcarle.konvert.api.KonvertTo
import io.mcarle.konvert.api.Mapping
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Poko
@KonvertTo(value = UIPost::class)
@KonvertTo(
  value = SavedPost::class,
  mappings = [Mapping(source = "submitter", target = "submitterName")],
)
class LobstersPostDetails(
  @Selector("ol.stories > li.story", attr = "data-shortid") val shortId: String,
  @Serializable(with = CreatedAtSerializer::class)
  @Selector("ol.stories > li.story div.byline > time", attr = "data-at-unix", defValue = "")
  val createdAt: String = "",
  @Selector("ol.stories > li.story span.link.h-cite > a") val title: String,
  @Selector("ol.stories > li.story span.link.h-cite > a", attr = "abs:href", defValue = "")
  val url: String = "",
  @Selector(
    "div.story_content div.story_text",
    textMode = SelectorHtmlTextMode.InnerHtml,
    defValue = "",
  )
  val description: String = "",
  @Serializable(with = CommentCountSerializer::class)
  @Selector(
    "ol.stories > li.story span.comments_label a",
    regex = "(\\d+ comments?|\\d+|no comments)",
    defValue = "0",
  )
  val commentCount: Int = 0,
  @Selector("ol.stories > li.story span.comments_label a", attr = "abs:href", defValue = "")
  val commentsUrl: String = "",
  @Selector(
    "ol.stories > li.story div.byline > a[href^=/~]:not([tabindex]):not([aria-hidden=true])"
  )
  @SerialName("submitter_user")
  val submitter: String,
  @Selector("ol.stories > li.story span.tags > a") val tags: List<String> = emptyList(),
  @Serializable(with = CommentsSerializer::class)
  @Selector("ol.comments > li.comments_subtree")
  val comments: List<Comment> = emptyList(),
  @Serializable(with = UserIsAuthorSerializer::class)
  @Selector(
    "ol.stories > li.story div.byline > a[href^=/~]:not([tabindex]):not([aria-hidden=true])",
    attr = "class",
    defValue = "",
  )
  @SerialName("user_is_author")
  val userIsAuthor: Boolean = false,
)
