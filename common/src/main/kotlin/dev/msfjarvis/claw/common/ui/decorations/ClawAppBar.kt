/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.ui.decorations

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.preview.DevicePreviews
import dev.msfjarvis.claw.common.ui.preview.ThemePreviews

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ClawAppBar(
  modifier: Modifier = Modifier,
  navigationIcon: @Composable () -> Unit = {},
  title: @Composable () -> Unit = {},
  actions: @Composable RowScope.() -> Unit = {},
) {
  TopAppBar(
    title = title,
    modifier = modifier.shadow(8.dp),
    colors = TopAppBarDefaults.smallTopAppBarColors(),
    navigationIcon = navigationIcon,
    actions = actions,
  )
}

@DevicePreviews
@ThemePreviews
@Composable
fun ClawAppBarPreview() {
  LobstersTheme {
    ClawAppBar(
      title = { Text("Claw", fontWeight = FontWeight.Bold) },
    )
  }
}
