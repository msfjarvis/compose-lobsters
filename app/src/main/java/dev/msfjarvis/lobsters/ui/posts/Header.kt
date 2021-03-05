package dev.msfjarvis.lobsters.ui.posts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.msfjarvis.lobsters.ui.theme.LobstersTheme
import java.time.Month
import java.util.Locale
import java.time.format.TextStyle as JTextStyle

@Composable
fun MonthHeader(month: Month) {
  Box(
    Modifier
      .fillMaxWidth()
      .background(MaterialTheme.colors.secondary)
      .wrapContentHeight()
      .padding(4.dp)
  ) {
    Text(
      text = month.getDisplayName(JTextStyle.FULL, Locale.getDefault()),
      style = MaterialTheme.typography.h5.copy(
        color = MaterialTheme.colors.onSecondary,
        textAlign = TextAlign.Center,
      ),
      modifier = Modifier.padding(horizontal = 12.dp),
    )
  }
}

@Preview
@Composable
fun MonthHeaderPreview() {
  LobstersTheme {
    MonthHeader(month = Month.JULY)
  }
}
