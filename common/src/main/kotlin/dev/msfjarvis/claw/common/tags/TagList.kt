/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.tags

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.deliveryhero.whetstone.compose.injectedViewModel
import dev.msfjarvis.claw.common.NetworkState.Error
import dev.msfjarvis.claw.common.NetworkState.Loading
import dev.msfjarvis.claw.common.NetworkState.Success
import dev.msfjarvis.claw.common.ui.ProgressBar
import dev.msfjarvis.claw.model.Tag
import kotlinx.collections.immutable.ImmutableList

@Composable
fun TagList(
  contentPadding: PaddingValues,
  modifier: Modifier = Modifier,
  viewModel: TagFilterViewModel = injectedViewModel(key = "tag_filter"),
) {
  val allTagsState = viewModel.allTags
  val filteredTags = viewModel.filteredTags

  LazyColumn(modifier = modifier.fillMaxWidth().padding(contentPadding)) {
    when (allTagsState) {
      is Loading -> {
        item {
          Box(modifier = Modifier.fillMaxSize()) {
            ProgressBar(modifier = Modifier.align(Alignment.Center))
          }
        }
      }
      is Error -> {
        item {
          Box(modifier = Modifier.fillMaxSize()) {
            Text("Failed to load tags", modifier = Modifier.align(Alignment.Center))
          }
        }
      }
      is Success<*> -> {
        @Suppress("UNCHECKED_CAST") val allTags = (allTagsState as Success<ImmutableList<Tag>>).data
        items(allTags) { tag ->
          val isSelected = filteredTags.contains(tag.tag)
          ListItem(
            headlineContent = { Text(tag.tag) },
            supportingContent = { Text(tag.description) },
            trailingContent = {
              Button(
                onClick = {
                  val updatedTags =
                    if (isSelected) {
                      filteredTags - tag.tag
                    } else {
                      filteredTags + tag.tag
                    }
                  viewModel.saveTags(updatedTags.toSet())
                }
              ) {
                Icon(
                  imageVector = if (isSelected) Icons.Default.Remove else Icons.Default.Add,
                  contentDescription = if (isSelected) "Remove" else "Add",
                )
              }
            },
          )
        }
      }
    }
  }
}
