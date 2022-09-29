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
