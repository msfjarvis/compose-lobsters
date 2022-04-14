package dev.msfjarvis.claw.android.ui.decorations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.msfjarvis.claw.android.ui.surfaceColorAtNavigationBarElevation
import java.time.Month
import java.time.format.TextStyle as JTextStyle
import java.util.Locale

@Composable
fun MonthHeader(month: Month) {
  Box(
    Modifier.fillMaxWidth()
      .wrapContentHeight()
      .background(MaterialTheme.colorScheme.surfaceColorAtNavigationBarElevation())
  ) {
    Text(
      text = month.getDisplayName(JTextStyle.FULL, Locale.getDefault()),
      style = MaterialTheme.typography.headlineSmall,
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
    )
  }
}
