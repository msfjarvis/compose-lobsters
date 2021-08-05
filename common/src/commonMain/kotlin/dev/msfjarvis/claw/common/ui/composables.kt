package dev.msfjarvis.claw.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun NetworkImage(
  url: String,
  contentDescription: String,
  modifier: Modifier,
)
