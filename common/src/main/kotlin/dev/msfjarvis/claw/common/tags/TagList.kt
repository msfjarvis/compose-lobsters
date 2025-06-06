/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.tags

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.preview.ThemePreviews
import dev.msfjarvis.claw.model.Tag
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.builtins.ListSerializer

@Composable
fun TagList(tags: ImmutableList<Tag>, modifier: Modifier = Modifier) {
  val selectedTags =
    rememberSaveable(serializer = ListSerializer(elementSerializer = Tag.serializer())) {
        mutableStateListOf()
      }
      .toMutableStateList()

  Column {
    tags.forEach { tag ->
      ListItem(
        headlineContent = { Text(tag.tag) },
        supportingContent = { Text(tag.description) },
        trailingContent = {
          val isSelected = selectedTags.contains(tag)
          Button(
            onClick = {
              if (isSelected) {
                selectedTags.remove(tag)
              } else {
                selectedTags.add(tag)
              }
            }
          ) {
            Icon(
              imageVector = if (isSelected) Icons.Default.Remove else Icons.Default.Add,
              contentDescription = if (isSelected) "Remove" else "Add",
            )
          }
        },
        modifier = modifier.fillMaxWidth(),
      )
    }
  }
}

@ThemePreviews
@Composable
private fun TagListPreview() {
  val sampleTags =
    persistentListOf(
      Tag(
        tag = "Kotlin",
        description = "Kotlin programming language",
        privileged = false,
        active = true,
        category = "languages",
        isMedia = false,
        hotnessMod = 0.0,
      ),
      Tag(
        tag = "Compose",
        description = "Jetpack Compose UI toolkit",
        privileged = false,
        active = true,
        category = "frameworks",
        isMedia = false,
        hotnessMod = 0.0,
      ),
      Tag(
        tag = "Android",
        description = "Android development",
        privileged = false,
        active = true,
        category = "platforms",
        isMedia = false,
        hotnessMod = 0.0,
      ),
    )

  LobstersTheme { Surface(modifier = Modifier.padding(16.dp)) { TagList(tags = sampleTags) } }
}
