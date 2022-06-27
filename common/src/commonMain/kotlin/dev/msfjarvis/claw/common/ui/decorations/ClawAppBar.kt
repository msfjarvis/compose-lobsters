package dev.msfjarvis.claw.common.ui.decorations

import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ClawAppBar(
  backgroundColor: Color,
  modifier: Modifier = Modifier,
  navigationIcon: @Composable () -> Unit = {},
  title: @Composable () -> Unit = {},
) {
  SmallTopAppBar(
    title = title,
    modifier = modifier,
    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = backgroundColor),
    navigationIcon = navigationIcon,
  )
}
