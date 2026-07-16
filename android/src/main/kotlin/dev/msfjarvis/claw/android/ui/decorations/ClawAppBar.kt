/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.decorations

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import dev.msfjarvis.claw.android.R
import dev.msfjarvis.claw.android.ui.navigation.Settings
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.preview.DevicePreviews
import dev.msfjarvis.claw.common.ui.preview.ThemePreviews

sealed interface ClawTopBarMode {
  data object Browsing : ClawTopBarMode

  class Searching(
    val query: String,
    val requestFocus: Boolean = true,
  ) : ClawTopBarMode
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClawAppBar(
  activity: Activity?,
  isTopLevel: Boolean,
  mode: ClawTopBarMode,
  navigateTo: (NavKey) -> Unit,
  popBackStack: () -> NavKey?,
  onStartSearch: () -> Unit,
  onDismissSearch: () -> Unit,
  onQueryChange: (String) -> Unit,
  onSearch: (String) -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable ColumnScope.() -> Unit,
) {
  when {
    !isTopLevel -> {
      TopAppBar(
        modifier = modifier.shadow(8.dp),
        navigationIcon = {
          IconButton(onClick = { if (popBackStack() == null) activity?.finish() }) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(R.string.go_back_to_previous_screen),
            )
          }
        },
        title = {},
      )
    }
    mode is ClawTopBarMode.Searching -> {
      val textFieldState = rememberTextFieldState()
      val searchBarState = rememberSearchBarState()
      val focusRequester = remember { FocusRequester() }
      Box(modifier = modifier) {
        ExpandedFullScreenSearchBar(
          state = searchBarState,
          inputField = {
            SearchBarDefaults.InputField(
              textFieldState = textFieldState,
              searchBarState = searchBarState,
              onSearch = onSearch,
              leadingIcon = {
                Icon(
                  imageVector = Icons.Filled.Search,
                  contentDescription = null,
                )
              },
              trailingIcon = {
                IconButton(
                  onClick = {
                    if (mode.query.isNotEmpty()) {
                      onQueryChange("")
                    } else {
                      onDismissSearch()
                    }
                  }
                ) {
                  Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.close_search),
                  )
                }
              },
            )
          },
          modifier = Modifier.shadow(8.dp).fillMaxWidth(),
          content = content,
        )
        LaunchedEffect(mode.requestFocus) {
          if (mode.requestFocus) {
            focusRequester.requestFocus()
          }
        }
      }
    }
    else -> {
      TopAppBar(
        modifier = modifier.shadow(8.dp),
        navigationIcon = {
          Icon(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(R.string.the_app_icon_for_claw),
            modifier = Modifier.size(48.dp),
          )
        },
        title = {
          Text(text = stringResource(R.string.app_name), fontWeight = FontWeight.Bold)
        },
        actions = {
          IconButton(onClick = onStartSearch) {
            Icon(
              imageVector = Icons.Filled.Search,
              contentDescription = stringResource(R.string.search_posts),
            )
          }
          IconButton(onClick = { navigateTo(Settings) }) {
            Icon(
              imageVector = Icons.Filled.Tune,
              contentDescription = stringResource(R.string.settings),
            )
          }
        },
      )
    }
  }
}

@Composable
private fun FakeSearchResults() {
  Text(text = "Fake result content")
}

@ThemePreviews
@DevicePreviews
@Composable
private fun ClawAppBarBrowsingPreview() {
  LobstersTheme {
    ClawAppBar(
      activity = null,
      isTopLevel = true,
      mode = ClawTopBarMode.Browsing,
      navigateTo = {},
      popBackStack = { null },
      onStartSearch = {},
      onDismissSearch = {},
      onQueryChange = {},
      onSearch = {},
      content = { FakeSearchResults() },
    )
  }
}

@ThemePreviews
@DevicePreviews
@Composable
private fun ClawAppBarSearchingPreview() {
  LobstersTheme {
    ClawAppBar(
      activity = null,
      isTopLevel = true,
      mode = ClawTopBarMode.Searching(query = "compose", requestFocus = false),
      navigateTo = {},
      popBackStack = { null },
      onStartSearch = {},
      onDismissSearch = {},
      onQueryChange = {},
      onSearch = {},
      content = { FakeSearchResults() },
    )
  }
}
