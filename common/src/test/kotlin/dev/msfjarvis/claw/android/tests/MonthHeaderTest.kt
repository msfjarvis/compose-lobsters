package dev.msfjarvis.claw.android.tests

import androidx.compose.material3.MaterialTheme
import dev.msfjarvis.claw.common.ui.decorations.MonthHeader
import java.time.Month
import org.junit.Test

class MonthHeaderTest : BasePaparazziTest() {
  @Test
  fun verify() {
    paparazzi.snapshot { MaterialTheme(colorScheme = theme.colors) { MonthHeader(Month.APRIL) } }
  }
}
