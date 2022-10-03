package dev.msfjarvis.claw.common.comments

/** Defines a contract to convert strings of HTML to Markdown. */
fun interface HTMLConverter {
  fun convertHTMLToMarkdown(html: String): String
}
