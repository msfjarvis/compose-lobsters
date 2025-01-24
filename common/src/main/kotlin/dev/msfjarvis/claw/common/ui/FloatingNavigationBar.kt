/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Taken from Chris Banes' amazing app Tivi
// https://github.com/chrisbanes/tivi/blob/836d596d74959f4235ca2395b5bbfdd6fd9c9a9e/ui/root/src/commonMain/kotlin/app/tivi/home/Home.kt#L173
@Composable
fun FloatingNavigationBar(
  modifier: Modifier = Modifier,
  shape: Shape = MaterialTheme.shapes.extraLarge,
  containerColor: Color = NavigationBarDefaults.containerColor,
  contentColor: Color = MaterialTheme.colorScheme.contentColorFor(containerColor),
  tonalElevation: Dp = NavigationBarDefaults.Elevation,
  content: @Composable RowScope.() -> Unit,
) {
  Surface(
    color = containerColor,
    contentColor = contentColor,
    tonalElevation = tonalElevation,
    shape = shape,
    border =
      BorderStroke(
        width = 0.5.dp,
        brush =
          Brush.verticalGradient(
            colors =
              listOf(
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
              )
          ),
      ),
    modifier = modifier,
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth().height(80.dp).selectableGroup(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      content = content,
    )
  }
}

@Preview
@Composable
private fun FloatingNavigationBarPreview() {
  FloatingNavigationBar {
    NavigationBarItem(
      selected = true,
      onClick = {},
      icon = { Icon(imageVector = Icons.Filled.HeartBroken, contentDescription = "Home") },
    )
    NavigationBarItem(
      selected = true,
      onClick = {},
      icon = { Icon(imageVector = Icons.Filled.BarChart, contentDescription = "Home") },
    )
    NavigationBarItem(
      selected = true,
      onClick = {},
      icon = { Icon(imageVector = Icons.Filled.BrokenImage, contentDescription = "Home") },
    )
  }
}
