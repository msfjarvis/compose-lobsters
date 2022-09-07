package dev.msfjarvis.claw.common.ui.decorations

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ClawAppBar(
  backgroundColor: Color,
  modifier: Modifier = Modifier,
  navigationIcon: @Composable () -> Unit = {},
  title: @Composable () -> Unit = {},
) {
  TopAppBar(
    title = title,
    modifier = modifier,
    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = backgroundColor),
    navigationIcon = navigationIcon,
  )
}
