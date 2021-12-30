package dev.msfjarvis.claw.android.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.msfjarvis.claw.android.R

@Composable
fun ClawAppBar(
  modifier: Modifier = Modifier,
) {
  SmallTopAppBar(
    title = {
      Text(
        text = stringResource(R.string.app_name),
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp),
      )
    },
    modifier = modifier,
  )
}
