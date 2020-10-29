package dev.msfjarvis.lobsters.ui

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.msfjarvis.lobsters.R
import dev.msfjarvis.lobsters.compose.utils.IconResource

@Composable
fun EmptyList(saved: Boolean) {
  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    if (saved) {
      IconResource(
        R.drawable.ic_favorite_border_24px,
        tint = savedTitleColor,
        modifier = Modifier.padding(16.dp)
      )
      Text(stringResource(R.string.no_saved_posts))
    } else {
      IconResource(R.drawable.ic_sync_problem_24px, modifier = Modifier.padding(16.dp))
      Text(stringResource(R.string.loading))
    }
  }
}
