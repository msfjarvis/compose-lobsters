package dev.msfjarvis.claw.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.kamel.image.KamelImage
import io.kamel.image.lazyImageResource

@Composable
actual fun NetworkImage(
  url: String,
  contentDescription: String,
  modifier: Modifier,
) {
  KamelImage(
    resource = lazyImageResource(url),
    contentDescription = contentDescription,
    modifier = Modifier.then(modifier),
    crossfade = true,
  )
}
