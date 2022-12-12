/*
 * Copyright © 2021-2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import coil.compose.AsyncImage

@Composable
internal fun NetworkImage(
  url: String?,
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
    modifier = modifier,
  )
}
