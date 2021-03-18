package dev.msfjarvis.lobsters.util

import java.time.Month
import kotlin.test.Test
import kotlin.test.assertEquals

class DateExtensionsTest {
  @Test
  fun `parses date correctly`() {
    val datetime = "2021-02-15T21:16:02.000-06:00".asZonedDateTime()
    assertEquals(Month.FEBRUARY, datetime.month)
  }
}
