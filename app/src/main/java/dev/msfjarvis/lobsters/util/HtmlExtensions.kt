package dev.msfjarvis.lobsters.util

import android.text.Html

/** Parses [this] as [Html] then strips all the formatting. */
fun String.toNormalizedHtml(): String {
  return Html.fromHtml(this).toString().trim()
}
