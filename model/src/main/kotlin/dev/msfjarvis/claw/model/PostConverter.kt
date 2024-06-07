/*
 * Copyright © 2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.model

import dev.msfjarvis.claw.database.local.NewestPosts
import io.mcarle.konvert.api.Konverter

@Konverter
interface PostConverter {
  fun toNewestPost(@Konverter.Source post: UIPost, page: Int): NewestPosts

  fun toNewestPost(
    @Konverter.Source post: LobstersPost,
    page: Int,
    isRead: Boolean,
    isSaved: Boolean,
  ): NewestPosts

  fun toUIPost(@Konverter.Source post: LobstersPost, isRead: Boolean, isSaved: Boolean): UIPost
}
