package dev.msfjarvis.claw.common.ui.decorations

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.preview.DevicePreviews
import dev.msfjarvis.claw.common.ui.preview.ThemePreviews
import dev.msfjarvis.claw.common.ui.surfaceColorAtNavigationBarElevation

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

@DevicePreviews
@ThemePreviews
@Composable
fun ClawAppBarPreview() {
  LobstersTheme {
    ClawAppBar(
      backgroundColor = MaterialTheme.colorScheme.surfaceColorAtNavigationBarElevation(),
      title = { Text("Claw", fontWeight = FontWeight.Bold) },
    )
  }
}
