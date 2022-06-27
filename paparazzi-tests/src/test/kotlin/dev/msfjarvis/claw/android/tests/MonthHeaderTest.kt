package dev.msfjarvis.claw.android.tests

import androidx.compose.material3.MaterialTheme
import app.cash.paparazzi.Paparazzi
import dev.msfjarvis.claw.common.theme.DarkThemeColors
import dev.msfjarvis.claw.common.theme.LightThemeColors
import dev.msfjarvis.claw.common.ui.decorations.MonthHeader
import kotlinx.datetime.Month
import org.junit.Rule
import org.junit.Test

class MonthHeaderTest {
  @get:Rule val paparazzi = Paparazzi()

  @Test
  fun light_mode() {
    paparazzi.snapshot {
      MaterialTheme(colorScheme = LightThemeColors) { MonthHeader(Month.APRIL) }
    }
  }

  @Test
  fun dark_mode() {
    paparazzi.snapshot {
      MaterialTheme(colorScheme = DarkThemeColors) { MonthHeader(Month.JANUARY) }
    }
  }
}
