package dev.msfjarvis.lobsters.util

import java.time.Month
import org.junit.Assert.*
import org.junit.Test

class DateExtensionsTest {
  @Test
  fun `parses date correctly`() {
    val datetime = "2021-02-15T21:16:02.000-06:00".asZonedDateTime()
    assertEquals(Month.FEBRUARY, datetime.month)
  }
}
