/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.decorations

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.paging.compose.collectAsLazyPagingItems
import com.deliveryhero.whetstone.compose.injectedViewModel
import dev.msfjarvis.claw.android.R
import dev.msfjarvis.claw.android.ui.lists.NetworkPosts
import dev.msfjarvis.claw.android.ui.navigation.Search
import dev.msfjarvis.claw.android.viewmodel.ClawViewModel
import dev.msfjarvis.claw.common.posts.PostActions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClawAppBar(
  activity: Activity?,
  isTopLevel: Boolean,
  navigateTo: (NavKey) -> Unit,
  postActions: PostActions,
  popBackStack: () -> NavKey?,
  modifier: Modifier = Modifier,
  viewModel: ClawViewModel = injectedViewModel(),
) {
  val listState = rememberLazyListState()
  val searchResults = viewModel.searchResults.collectAsLazyPagingItems()
  val textFieldState = rememberTextFieldState()
  val searchBarState = rememberSearchBarState()
  val scope = rememberCoroutineScope()
  val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()
  val inputField =
    @Composable {
      SearchBarDefaults.InputField(
        modifier = Modifier,
        searchBarState = searchBarState,
        textFieldState = textFieldState,
        onSearch = {
          scope.launch { searchBarState.animateToCollapsed() }
          viewModel.searchQuery = it
          searchResults.refresh()
        },
        placeholder = { Text("Search...") },
        leadingIcon = {
          if (searchBarState.currentValue == SearchBarValue.Expanded) {
            TooltipBox(
              positionProvider =
                TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
              tooltip = { PlainTooltip { Text("Back") } },
              state = rememberTooltipState(),
            ) {
              IconButton(onClick = { scope.launch { searchBarState.animateToCollapsed() } }) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
              }
            }
          } else {
            Icon(Icons.Default.Search, contentDescription = null)
          }
        },
        trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
      )
    }
  AppBarWithSearch(
    scrollBehavior = scrollBehavior,
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
    state = searchBarState,
    inputField = inputField,
    colors = SearchBarDefaults.appBarWithSearchColors(appBarContainerColor = Color.Transparent),
  )
  ExpandedFullScreenSearchBar(state = searchBarState, inputField = inputField) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

    }
  }
}
