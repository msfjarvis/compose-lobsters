/*
 * Copyright © 2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.lists

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.msfjarvis.claw.data.store.NewestPostsStore

@Suppress("Unused", "UNUSED_PARAMETER")
@Composable
fun StorePagingList(store: NewestPostsStore, modifier: Modifier = Modifier) {
  LazyColumn(modifier = modifier) {
    //    itemsIndexed(items) { index, item ->
    //      if (index == items.size - 1) {
    //        LaunchedEffect(key1 = currentPage) {
    //          loadData()
    //        }
    //      }
    //    }
  }
}
