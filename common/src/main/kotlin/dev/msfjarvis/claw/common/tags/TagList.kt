/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.tags

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalFlexBoxApi
import androidx.compose.foundation.layout.FlexBox
import androidx.compose.foundation.layout.FlexDirection
import androidx.compose.foundation.layout.FlexWrap
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.msfjarvis.claw.common.NetworkState.Error
import dev.msfjarvis.claw.common.NetworkState.Loading
import dev.msfjarvis.claw.common.NetworkState.Success
import dev.msfjarvis.claw.common.ui.ProgressBar
import dev.msfjarvis.claw.model.Tag
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalFlexBoxApi::class)
@Composable
fun TagList(
  contentPadding: PaddingValues,
  modifier: Modifier = Modifier,
  viewModel: TagFilterViewModel = metroViewModel(key = "tag_filter"),
) {
  val allTagsState = viewModel.allTags
  val filteredTags by viewModel.filteredTags.collectAsStateWithLifecycle(emptySet())

  when (allTagsState) {
    is Loading -> {
      Box(modifier = modifier.fillMaxSize().padding(contentPadding)) {
        ProgressBar(modifier = Modifier.align(Alignment.Center))
      }
    }
    is Error -> {
      Box(modifier = modifier.fillMaxSize().padding(contentPadding)) {
        Text("Failed to load tags", modifier = Modifier.align(Alignment.Center))
      }
    }
    is Success<*> -> {
      @Suppress("UNCHECKED_CAST") val allTags = (allTagsState as Success<ImmutableList<Tag>>).data
      Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).padding(contentPadding)
      ) {
        Text(
          text = "Posts with selected tags will be filtered out of hottest/newest/search feeds",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onBackground,
        )
        FlexBox(
          modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
          config = {
            columnGap = 8.dp
            rowGap = 8.dp
            direction = FlexDirection.Row
            wrap = FlexWrap.Wrap
          },
        ) {
          allTags
            .sortedBy { it.tag }
            .forEach { tag ->
              val isSelected = filteredTags.contains(tag.tag)
              FilterChip(
                selected = isSelected,
                leadingIcon =
                  if (isSelected) {
                    {
                      Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Done icon",
                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                      )
                    }
                  } else {
                    null
                  },
                onClick = {
                  val updatedTags =
                    if (isSelected) {
                      filteredTags - tag.tag
                    } else {
                      filteredTags + tag.tag
                    }
                  viewModel.saveTags(updatedTags.toSet())
                },
                label = { Text(tag.tag) },
              )
            }
        }
      }
    }
  }
}
