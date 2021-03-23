package dev.msfjarvis.lobsters.ui.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.msfjarvis.lobsters.R
import dev.msfjarvis.lobsters.ui.navigation.Destination
import dev.msfjarvis.lobsters.util.IconResource
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LobstersTopAppBar(
  currentDestination: Destination,
  toggleSortingOrder: suspend () -> Unit,
) {
  val scope = rememberCoroutineScope()
  TopAppBar(
    title = {
      Text(
        text = stringResource(id = R.string.app_name),
        modifier = Modifier.padding(vertical = 8.dp),
      )
    },
    actions = {
      if (currentDestination == Destination.Saved) {
        IconResource(
          resourceId = R.drawable.ic_sort_24px,
          contentDescription = stringResource(id = R.string.change_sorting_order),
          modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clickable { scope.launch { toggleSortingOrder() } },
        )
      }
    }
  )
}
