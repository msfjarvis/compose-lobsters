/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.parser.internal

import com.fleeksoft.ksoup.Ksoup
import dev.msfjarvis.claw.parser.model.User

private const val BASE_URL = "https://lobste.rs"

internal fun parseUser(html: String): User {
  val document = Ksoup.parse(html, baseUri = BASE_URL)
  return User(
    username = document.select("#inside > h1").text(),
    about = document.select("section.profile .shorten_first_p").html(),
    invitedBy =
      document
        .select("section.profile .labelled_grid label:contains(Joined) + span a[href^=/~/]")
        .text()
        .ifBlank { null },
    avatarUrl = document.select("section.profile #gravatar img.avatar").attr("abs:src"),
    createdAt =
      document.select("section.profile .labelled_grid label:contains(Joined) + span time").text(),
  )
}
