/*
 * Copyright © 2021-2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments

/** Defines a contract to convert strings of HTML to Markdown. */
fun interface HTMLConverter {
  fun convertHTMLToMarkdown(html: String): String
}
