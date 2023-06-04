/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import coil.compose.AsyncImage

@Composable
internal fun NetworkImage(
  url: String?,
  placeholder: ImageVector,
  contentDescription: String,
  modifier: Modifier = Modifier,
) {
  val painter = rememberVectorPainter(placeholder)
  AsyncImage(
    model = url,
    placeholder = painter,
    error = painter,
    fallback = painter,
    contentDescription = contentDescription,
    modifier = modifier,
  )
}
