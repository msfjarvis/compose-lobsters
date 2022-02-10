package dev.msfjarvis.claw.common.ui

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
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
      rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
          .data(data = url)
          .transformations(CircleCropTransformation())
          .crossfade(true)
          .build()
      ),
    contentDescription = contentDescription,
    modifier = modifier,
  )
}
