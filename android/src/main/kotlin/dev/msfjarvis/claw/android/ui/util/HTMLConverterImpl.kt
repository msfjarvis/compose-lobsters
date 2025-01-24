/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.util

import androidx.compose.runtime.Stable
import dev.msfjarvis.claw.common.comments.HTMLConverter
import io.github.furstenheim.CopyDown
import javax.inject.Inject

@Stable
class HTMLConverterImpl @Inject constructor() : HTMLConverter {
  private val copydown = CopyDown()

  override fun convertHTMLToMarkdown(html: String): String {
    return copydown.convert(html)
  }
}
