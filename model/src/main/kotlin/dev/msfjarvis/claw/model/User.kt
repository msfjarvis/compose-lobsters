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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Poko
class User(
  @Selector("#inside > h1") val username: String,
  @Selector(
    "section.profile .shorten_first_p",
    textMode = SelectorHtmlTextMode.InnerHtml,
    defValue = "",
  )
  val about: String = "",
  @Serializable(with = EmptyStringAsNullSerializer::class)
  @Selector(
    "section.profile .labelled_grid label:contains(Joined) + span a[href^=/~/]",
    defValue = "",
  )
  @SerialName("invited_by_user")
  val invitedBy: String? = null,
  @Selector("section.profile #gravatar img.avatar", attr = "abs:src", defValue = "")
  val avatarUrl: String = "",
  @Selector("section.profile .labelled_grid label:contains(Joined) + span time", defValue = "")
  val createdAt: String = "",
)
