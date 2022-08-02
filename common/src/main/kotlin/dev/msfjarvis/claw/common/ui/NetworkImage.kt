package dev.msfjarvis.claw.common.ui

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage

@Composable
fun NetworkImage(
  url: String,
  contentDescription: String,
  modifier: Modifier,
) {
  AsyncImage(
    model = url,
    contentDescription = contentDescription,
    modifier = modifier.clip(CircleShape)
  )
}
