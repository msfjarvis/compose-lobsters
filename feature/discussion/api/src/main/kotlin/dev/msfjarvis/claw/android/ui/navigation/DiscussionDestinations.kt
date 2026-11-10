/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.navigation

import android.os.Parcelable
import androidx.navigation3.runtime.NavKey
import dev.msfjarvis.claw.navigation.api.NonStackable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Comments(val postId: String, val commentsUrl: String) :
  NavKey, Parcelable, NonStackable {
  override fun isSameStackEntry(other: NavKey): Boolean =
    other is Comments && postId == other.postId
}

@Parcelize
@Serializable
class Reply(
  val postId: String,
  val commentId: String,
  // This can potentially be massive, but I'm trusting that anybody wanting to reply to a wall of
  // text will probably want a physical keyboard to write their rebuttal.
  val commentText: String,
) : NavKey, Parcelable
