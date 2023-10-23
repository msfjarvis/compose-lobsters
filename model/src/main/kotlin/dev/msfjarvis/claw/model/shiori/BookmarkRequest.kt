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
class BookmarkRequest(
  val url: String,
  val createArchive: Boolean,
  val public: Int,
  val tags: List<Tag>,
  val title: String,
  val excerpt: String,
)
