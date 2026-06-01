/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments.reply

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import java.util.concurrent.ConcurrentHashMap

@Inject
@SingleIn(AppScope::class)
class CommentTextHolder {
  private val texts = ConcurrentHashMap<String, String>()

  fun store(commentId: String, text: String) {
    texts[commentId] = text
  }

  fun retrieve(commentId: String): String = texts[commentId].orEmpty()
}
