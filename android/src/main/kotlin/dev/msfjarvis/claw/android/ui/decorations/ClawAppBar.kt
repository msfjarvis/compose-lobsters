package dev.msfjarvis.claw.android.ui.decorations

import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ClawAppBar(
  backgroundColor: Color,
  scrollBehavior: TopAppBarScrollBehavior? = null,
  navigationIcon: @Composable () -> Unit = {},
  title: @Composable () -> Unit = {},
  modifier: Modifier = Modifier,
) {
  SmallTopAppBar(
    title = title,
    modifier = modifier,
    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = backgroundColor),
    scrollBehavior = scrollBehavior,
    navigationIcon = navigationIcon
  )
}
