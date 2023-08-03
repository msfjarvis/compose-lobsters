/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.store.utils

import dev.msfjarvis.claw.database.local.CachedNewestPost
import dev.msfjarvis.claw.model.LobstersPost

internal fun LobstersPost.toCachedNewest(page: Int): CachedNewestPost {
  return CachedNewestPost(
    pageNumber = page,
    shortId = shortId,
    title = title,
    url = url,
    description = description,
    commentCount = commentCount,
    commentsUrl = commentsUrl,
    tags = tags,
  )
}
