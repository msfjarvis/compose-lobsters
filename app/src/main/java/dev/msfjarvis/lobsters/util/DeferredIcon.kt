package dev.msfjarvis.lobsters.util

import androidx.annotation.DrawableRes
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

@Composable
fun IconResource(
  @DrawableRes resourceId: Int,
  contentDescription: String,
  modifier: Modifier = Modifier,
  tint: Color = LocalContentColor.current
) {
  val painter = painterResource(resourceId)
  Icon(
    painter = painter,
    modifier = modifier,
    tint = tint,
    contentDescription = contentDescription,
  )
}
