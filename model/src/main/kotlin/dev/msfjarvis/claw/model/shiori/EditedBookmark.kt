/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.model.shiori

import dev.drewhamilton.poko.Poko
import kotlinx.serialization.Serializable

@Serializable
@Poko
class EditedBookmark(
  val id: Int,
  val url: String,
  val title: String,
  val excerpt: String? = null,
  val author: String? = null,
  val public: Int? = null,
  val modified: String? = null,
  val imageURL: String? = null,
  val hasContent: Boolean? = null,
  val hasArchive: Boolean? = null,
  val tags: List<Tag>? = null,
  val createArchive: Boolean? = null,
)
