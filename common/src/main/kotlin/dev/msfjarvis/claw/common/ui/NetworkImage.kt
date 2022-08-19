package dev.msfjarvis.claw.common.ui

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import coil.compose.AsyncImage

@Composable
fun NetworkImage(
  url: String,
  placeholder: Painter,
  contentDescription: String,
  modifier: Modifier = Modifier,
) {
  AsyncImage(
    model = url,
    placeholder = placeholder,
    error = placeholder,
    fallback = placeholder,
    contentDescription = contentDescription,
    modifier = modifier.clip(CircleShape)
  )
}
