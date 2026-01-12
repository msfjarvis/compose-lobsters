/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.net.toUri
import be.digitalia.compose.htmlconverter.HtmlStyle
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.common.BuildConfig
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.preview.ThemePreviews

@Composable
internal fun ThemedRichText(text: String, modifier: Modifier = Modifier) {
  val linkBackground = MaterialTheme.colorScheme.surfaceVariant
  val linkColor = MaterialTheme.colorScheme.onSurface
  val convertedText =
    remember(text) {
      val annotatedString =
        htmlToAnnotatedString(
          // Lobsters seems to insert literal newlines between paragraphs for some reason which
          // makes
          // the resultant view come out rather ugly. We strip those out by hand and let the
          // standard
          // paragraph formatting handle separating individual blocks.
          html = text.replace("</p>\\n<p>", "</p><p>"),
          style =
            HtmlStyle(
              textLinkStyles =
                TextLinkStyles(
                  style =
                    SpanStyle(
                      background = linkBackground,
                      color = linkColor,
                      fontWeight = FontWeight.Bold,
                      textDecoration = TextDecoration.Underline,
                    )
                )
            ),
        )
      rewriteLobstersLinksToDeepLinks(annotatedString)
    }
  Text(
    text = convertedText,
    color = contentColorFor(MaterialTheme.colorScheme.background),
    style = MaterialTheme.typography.bodyLarge.copy(lineBreak = LineBreak.Paragraph),
    modifier = modifier,
  )
}

@OptIn(ExperimentalTextApi::class)
private fun rewriteLobstersLinksToDeepLinks(annotatedString: AnnotatedString): AnnotatedString {
  val linkAnnotations = annotatedString.getLinkAnnotations(0, annotatedString.length)

  if (linkAnnotations.isEmpty()) {
    return annotatedString
  }

  return AnnotatedString.Builder(annotatedString)
    .apply {
      linkAnnotations.forEach { annotation ->
        val link = annotation.item
        if (link is LinkAnnotation.Url) {
          val url = link.url
          val rewrittenUrl = rewriteUrlIfLobstersPost(url)

          addLink(
            LinkAnnotation.Url(
              rewrittenUrl,
              styles = link.styles,
              linkInteractionListener = link.linkInteractionListener,
            ),
            start = annotation.start,
            end = annotation.end,
          )
        }
      }
    }
    .toAnnotatedString()
}

private val lobstersUri = LobstersApi.BASE_URL.toUri()

private fun rewriteUrlIfLobstersPost(url: String): String {
  return try {
    val uri = url.toUri()
    if (
      uri.scheme in setOf("http", "https") &&
        uri.host == lobstersUri.host &&
        uri.path?.startsWith("/s/") == true
    ) {
      val pathSegments = uri.path?.split("/").orEmpty()
      if (pathSegments.size >= 3) {
        val shortId = pathSegments[2]
        if (shortId.isNotEmpty()) {
          return "${BuildConfig.DEEPLINK_SCHEME}://comments/$shortId"
        }
      }
    }
    url
  } catch (_: Exception) {
    url
  }
}

@ThemePreviews
@Composable
internal fun ThemedRichTextPreview() {
  val text =
    """
    <p>I think you need middleman, as it’s a good idea to keep the separation between the content and presentation, as you might want to change the presentation. But it indeed feels like a good idea to use HTML as your middleman.</p><p>In other words, I don’t think it’s a good idea to write <code>&lt;pre&gt;&lt;code&gt;func main()&lt;/code&gt;&lt;/pre&gt;</code>, as, if you later decide that you want syntax highlighting, or line numbers, or something else that requires a different html-structure, you’d have to re-do your content.</p>\n<p>But you could write <code>&lt;code-go&gt;func main()&lt;/code-go&gt;</code> and then have an “HTML preprocessor” that reads that and lowers to <code>&lt;pre&gt;&lt;code&gt;</code> with or without syntax highlighting. Honestly, I <em>wish</em> I had that trick in my toolbox in 2017, I might have used it for my blog.</p>\n<p>That being said, often you do want to keep the source from be readable as is, and its <em>nice</em> to have concrete syntax for <code>&lt;p&gt;</code>, <code>&lt;li&gt;</code>, <code>&lt;strong&gt;</code> and such. There’s no inherent contradiction between markdown benefit of being readable in the source code, and HTML benefit of giving you generic attributed trees. You can have both with some tasteful design.</p>
    <h1>Hello <strong>HTML Converter</strong> for Compose</h1>
    <p>This the first paragraph of the sample app running on <strong>Nothing</strong>!</p>
    <ul>
        <li><strong>Bold</strong></li>
        <li><em>Italic</em></li>
        <li><u>Underline</u></li>
        <li><del>Strikethrough</del></li>
        <li><code>Code</code></li>
        <li><a href="https://www.wikipedia.org/">Hyperlink with custom styling</a></li>
        <li><big>Bigger</big> and <small>smaller</small> text</li>
        <li><sup>Super</sup>text and <sub>sub</sub>text</li>
        <li>A nested ordered list:
            <ol>
                <li>Item 1</li>
                <li>Item 2</li>
            </ol>
        </li>
    </ul>
    <dl>
        <dt>Term</dt>
        <dd>Description.</dd>
    </dl>
    A few HTML entities: &raquo; &copy; &laquo; &check;
    <blockquote>A blockquote, indented relatively to the main text.</blockquote>
    <pre><code class="language-toml">Preformatted text, preserving
    line breaks...    and spaces.</code></pre>
    <p><code>Inline code example.</code> You reached the end of the document.<br />Thank you for reading!</p>
    """
      .trimIndent()

  LobstersTheme { ThemedRichText(text = text) }
}
