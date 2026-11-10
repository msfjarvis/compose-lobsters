/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.lists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import dev.msfjarvis.claw.android.ui.screens.AnubisChallengeDialog
import dev.msfjarvis.claw.core.network.CHROME_USER_AGENT
import dev.msfjarvis.claw.core.network.SearchChallengeException
import dev.msfjarvis.claw.feature.search.impl.R
import dev.msfjarvis.claw.model.UIPost

internal fun isSearchChallenge(loadState: LoadState): Boolean =
  (loadState as? LoadState.Error)?.error is SearchChallengeException

internal fun retryAfterChallenge(
  cookie: String,
  saveCookie: (String) -> Unit,
  retry: () -> Unit,
) {
  saveCookie(cookie)
  retry()
}

@Composable
fun SearchResultsList(
  lazyPagingItems: LazyPagingItems<UIPost>,
  onClearanceCookie: (String) -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable (Modifier) -> Unit,
) {
  val refreshLoadState by rememberUpdatedState(lazyPagingItems.loadState.refresh)
  val hasNoResults = lazyPagingItems.itemCount == 0 && refreshLoadState is LoadState.NotLoading
  val challenged =
    isSearchChallenge(refreshLoadState) || isSearchChallenge(lazyPagingItems.loadState.append)
  var showChallenge by remember { mutableStateOf(false) }

  if (showChallenge) {
    AnubisChallengeDialog(
      userAgent = CHROME_USER_AGENT,
      onDismiss = { showChallenge = false },
      onCookie = { cookie ->
        retryAfterChallenge(cookie, onClearanceCookie, lazyPagingItems::retry)
        showChallenge = false
      },
    )
  }

  Box(modifier = modifier.fillMaxSize()) {
    if (challenged) {
      Column(
        modifier = Modifier.align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Text(stringResource(R.string.search_challenge_message))
        Button(onClick = { showChallenge = true }) {
          Text(stringResource(R.string.search_challenge_button))
        }
      }
    } else if (hasNoResults) {
      Text(
        text = stringResource(R.string.no_search_results),
        modifier = Modifier.align(Alignment.Center),
      )
    } else {
      content(Modifier.fillMaxSize())
    }
  }
}
