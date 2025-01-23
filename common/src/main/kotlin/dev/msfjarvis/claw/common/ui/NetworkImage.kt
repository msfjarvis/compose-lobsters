/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.RenderVectorGroup
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import coil3.compose.AsyncImage

@Composable
internal fun NetworkImage(
  url: String?,
  placeholder: ImageVector,
  contentDescription: String,
  modifier: Modifier = Modifier,
  placeholderTintColor: Color? = null,
) {
  val painter =
    rememberVectorPainter(
      defaultHeight = placeholder.defaultHeight,
      defaultWidth = placeholder.defaultWidth,
      viewportWidth = placeholder.viewportWidth,
      viewportHeight = placeholder.viewportHeight,
      name = placeholder.name,
      tintColor = placeholderTintColor ?: Color.Unspecified,
      tintBlendMode = placeholder.tintBlendMode,
      autoMirror = placeholder.autoMirror,
      content = { _, _ -> RenderVectorGroup(group = placeholder.root) },
    )
  AsyncImage(
    model = url,
    placeholder = painter,
    error = painter,
    fallback = painter,
    contentDescription = contentDescription,
    modifier = modifier,
  )
}
