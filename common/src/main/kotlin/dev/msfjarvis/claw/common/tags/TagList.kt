/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.tags

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalFlexBoxApi
import androidx.compose.foundation.layout.FlexBox
import androidx.compose.foundation.layout.FlexDirection
import androidx.compose.foundation.layout.FlexWrap
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Block
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.msfjarvis.claw.common.BuildConfig
import dev.msfjarvis.claw.common.NetworkState.Error
import dev.msfjarvis.claw.common.NetworkState.Loading
import dev.msfjarvis.claw.common.NetworkState.Success
import dev.msfjarvis.claw.common.ui.ProgressBar
import dev.msfjarvis.claw.model.Tag
import dev.zacsweers.metrox.viewmodel.metroViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalFlexBoxApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TagList(
  contentPadding: PaddingValues,
  modifier: Modifier = Modifier,
  viewModel: TagFilterViewModel = metroViewModel(key = "tag_filter"),
) {
  val allTagsState = viewModel.allTags
  val filteredTags by viewModel.filteredTags.collectAsStateWithLifecycle(emptySet())
  val tagBlocks by viewModel.tagBlocks.collectAsStateWithLifecycle(emptyList())
  var showDatePicker by remember { mutableStateOf(false) }
  var selectedTagForDatePicker by remember { mutableStateOf<String?>(null) }

  selectedTagForDatePicker?.let { tagName ->
    if (showDatePicker) {
      val tomorrow = Instant.now().plusMillis(24 * 60 * 60 * 1000)
      val datePickerState =
        rememberDatePickerState(initialSelectedDateMillis = tomorrow.toEpochMilli())

      DatePickerDialog(
        onDismissRequest = {
          showDatePicker = false
          selectedTagForDatePicker = null
        },
        confirmButton = {
          TextButton(
            onClick = {
              val selectedDateMillis = datePickerState.selectedDateMillis
              if (selectedDateMillis != null) {
                viewModel.saveTagBlock(tagName, selectedDateMillis)
              }
              showDatePicker = false
              selectedTagForDatePicker = null
            }
          ) {
            Text("Block Until")
          }
        },
        dismissButton = {
          TextButton(
            onClick = {
              viewModel.saveTagBlock(tagName, null)
              showDatePicker = false
              selectedTagForDatePicker = null
            }
          ) {
            Text("Block Forever")
          }
        },
      ) {
        DatePicker(state = datePickerState)
      }
    }
  }

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
        if (BuildConfig.DEBUG) {
          TagExpirationTestControls(onTriggerCleanup = { viewModel.triggerCleanupNow() })
        }
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
              val tagBlock = tagBlocks.find { it.tag == tag.tag }

              FilterChip(
                selected = isSelected,
                leadingIcon =
                  if (isSelected) {
                    {
                      Icon(
                        imageVector =
                          if (tagBlock?.isPermanent == false) Icons.Filled.AccessTime
                          else Icons.Filled.Block,
                        contentDescription =
                          if (tagBlock?.isPermanent == false) "Temporary block"
                          else "Permanent block",
                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                      )
                    }
                  } else {
                    null
                  },
                onClick = {
                  if (isSelected) {
                    viewModel.removeTagBlock(tag.tag)
                  } else {
                    selectedTagForDatePicker = tag.tag
                    showDatePicker = true
                  }
                },
                label = {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                  ) {
                    Text(tag.tag)
                    if (isSelected && tagBlock != null) {
                      tagBlock.expirationMillis?.let { expirationMillis ->
                        Text(
                          text = " • ${formatDate(expirationMillis)}",
                          style = MaterialTheme.typography.bodySmall,
                          color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                      }
                    }
                  }
                },
              )
            }
        }
      }
    }
  }
}

private fun formatDate(millis: Long): String {
  val instant = Instant.ofEpochMilli(millis)
  val formatter = DateTimeFormatter.ofPattern("MMM dd").withZone(ZoneId.systemDefault())
  return formatter.format(instant)
}
