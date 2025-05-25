/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.decorations

import android.app.Activity
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import dev.msfjarvis.claw.android.R
import dev.msfjarvis.claw.android.ui.navigation.Search
import dev.msfjarvis.claw.android.ui.navigation.Settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClawAppBar(
  activity: Activity?,
  isTopLevel: Boolean,
  navigateTo: (NavKey) -> Unit,
  popBackStack: () -> NavKey?,
  modifier: Modifier = Modifier,
) {
  TopAppBar(
    modifier = modifier.shadow(8.dp),
    navigationIcon = {
      if (!isTopLevel) {
        IconButton(onClick = { if (popBackStack() == null) activity?.finish() }) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Go back to previous screen",
          )
        }
      } else {
        Icon(
          painter = painterResource(id = R.drawable.ic_launcher_foreground),
          contentDescription = "The app icon for Claw",
          modifier = Modifier.size(48.dp),
        )
      }
    },
    title = {
      if (isTopLevel) {
        Text(text = stringResource(R.string.app_name), fontWeight = FontWeight.Bold)
      }
    },
    actions = {
      if (isTopLevel) {
        IconButton(onClick = { navigateTo(Search) }) {
          Icon(imageVector = Icons.Filled.Search, contentDescription = "Search posts")
        }
        IconButton(onClick = { navigateTo(Settings) }) {
          Icon(imageVector = Icons.Filled.Tune, contentDescription = "Settings")
        }
      }
    },
  )
}
