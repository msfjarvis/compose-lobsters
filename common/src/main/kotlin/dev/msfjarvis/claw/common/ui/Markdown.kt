/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.ui

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichText
import com.halilibo.richtext.ui.RichTextStyle
import com.halilibo.richtext.ui.string.RichTextStringStyle
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.preview.ThemePreviews

@Composable
fun ThemedRichText(
  text: String,
  modifier: Modifier = Modifier,
) {
  val linkStyle =
    SpanStyle(
      background = MaterialTheme.colorScheme.surfaceVariant,
      color = MaterialTheme.colorScheme.onSurface,
      fontWeight = FontWeight.Bold,
      textDecoration = TextDecoration.Underline,
    )
  val stringStyle = RichTextStringStyle.Default.copy(linkStyle = linkStyle)
  CompositionLocalProvider(
    LocalTextStyle provides MaterialTheme.typography.bodyLarge,
    LocalContentColor provides MaterialTheme.colorScheme.onBackground,
  ) {
    RichText(
      modifier = modifier,
      style = RichTextStyle.Default.copy(stringStyle = stringStyle),
    ) {
      Markdown(text)
    }
  }
}

@ThemePreviews
@Composable
fun ThemedRichTextPreview() {
  val text =
    """
      ### Heading
      This is a paragraph body

      ```
      This is a code block
      ```

      This is an `inline code block`

      [This is a link](https://github.com/msfjarvis/compose-lobsters)

      ![Image](https://avatars.githubusercontent.com/u/13348378?v=4)
  """
      .trimIndent()

  LobstersTheme { ThemedRichText(text = text) }
}
