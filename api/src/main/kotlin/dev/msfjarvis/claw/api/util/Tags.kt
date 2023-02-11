/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api.util

class Tags {
  private var tags: MutableList<String> = emptyList<String>().toMutableList()

  fun addTag(tag: String) {
    this.tags.add(tag)
  }

  fun removeTag(tag: String) {
    this.tags.remove(tag)
  }

  override fun toString(): String {
    return tags.joinToString(",")
  }
}
