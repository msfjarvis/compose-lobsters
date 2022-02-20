package dev.msfjarvis.claw.common.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Divider(modifier: Modifier = Modifier) {
  androidx.compose.material.Divider(
    color = MaterialTheme.colorScheme.onBackground.copy(alpha = DividerAlpha),
    modifier = modifier,
  )
}

private const val DividerAlpha = 0.15f
