/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.model

class FiltersPage(
  val authenticityToken: String,
  val tags: List<Tag>,
  val blockedTags: Set<String>,
)
