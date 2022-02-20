package dev.msfjarvis.claw.android.ui.decorations

import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.msfjarvis.claw.android.R

@Composable
fun ClawAppBar(
  backgroundColor: Color,
  scrollBehavior: TopAppBarScrollBehavior? = null,
  modifier: Modifier = Modifier,
) {
  SmallTopAppBar(
    title = {
      Text(
        text = stringResource(R.string.app_name),
        fontWeight = FontWeight.Bold,
      )
    },
    modifier = modifier,
    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = backgroundColor),
    scrollBehavior = scrollBehavior,
  )
}
