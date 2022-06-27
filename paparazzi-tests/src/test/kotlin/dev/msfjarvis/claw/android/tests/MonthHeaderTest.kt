package dev.msfjarvis.claw.android.tests

import androidx.compose.material3.MaterialTheme
import app.cash.paparazzi.Paparazzi
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import dev.msfjarvis.claw.common.ui.decorations.MonthHeader
import kotlinx.datetime.Month
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class MonthHeaderTest {
  @get:Rule val paparazzi = Paparazzi()

  @Test
  fun verify(@TestParameter theme: Theme) {
    paparazzi.snapshot(name = theme.name) {
      MaterialTheme(colorScheme = theme.colors) { MonthHeader(Month.APRIL) }
    }
  }
}
