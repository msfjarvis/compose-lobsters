package dev.msfjarvis.claw.common.comments

import androidx.compose.runtime.staticCompositionLocalOf

/** Defines a contract to convert strings of HTML to Markdown. */
fun interface HTMLConverter {
  fun convertHTMLToMarkdown(html: String): String
}

val LocalHTMLConverter = staticCompositionLocalOf<HTMLConverter> { error("To be provided") }
