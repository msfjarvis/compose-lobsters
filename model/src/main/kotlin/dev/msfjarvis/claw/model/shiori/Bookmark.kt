/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.model.shiori

import dev.drewhamilton.poko.Poko
import kotlinx.serialization.Serializable

@Serializable
@Poko
class Bookmark(
  val id: Int,
  val url: String,
  val title: String,
  val excerpt: String,
  val author: String,
  val public: Int,
  val modified: String,
  val imageURL: String,
  val hasContent: Boolean,
  val hasArchive: Boolean,
  val tags: List<Tag>,
  val createArchive: Boolean,
)
