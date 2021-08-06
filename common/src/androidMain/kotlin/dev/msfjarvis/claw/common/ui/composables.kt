package dev.msfjarvis.claw.common.ui

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation

@Composable
@OptIn(ExperimentalCoilApi::class)
actual fun NetworkImage(
  url: String,
  contentDescription: String,
  modifier: Modifier,
) {
  Image(
    painter =
      rememberImagePainter(
        data = url,
        builder = {
          transformations(CircleCropTransformation())
          crossfade(true)
        },
      ),
    contentDescription = contentDescription,
    modifier = Modifier.then(modifier),
  )
}
