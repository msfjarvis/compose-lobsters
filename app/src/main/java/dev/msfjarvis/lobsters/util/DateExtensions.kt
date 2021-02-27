package dev.msfjarvis.lobsters.util

import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Locale

/**
 * Parses a given [String] into a [ZonedDateTime]. This method only works on dates in the format
 * returned by the Lobsters API, and is not a general purpose parsing solution.
 */
fun String.asZonedDateTime(): ZonedDateTime {
  val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)
  val date = checkNotNull(sdf.parse(this))
  return date.toInstant().atZone(ZoneId.systemDefault())
}
