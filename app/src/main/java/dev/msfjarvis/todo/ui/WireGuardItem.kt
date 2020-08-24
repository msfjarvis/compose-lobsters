package dev.msfjarvis.todo.ui

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.msfjarvis.todo.data.TodoItem

@Suppress("Unused")
@Composable
fun LazyItemScope.WireGuardItem(item: TodoItem) {
  var checked by remember { mutableStateOf(false) }
  Row(
    modifier = Modifier.padding(vertical = 8.dp).fillParentMaxWidth(),
  ) {
    Card(
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier.fillParentMaxWidth(),
      backgroundColor = MaterialTheme.colors.secondary
    ) {
      ListItem(
        text = {
          Text(
            text = item.title,
            style = TextStyle(
              fontSize = 20.sp,
              textAlign = TextAlign.Left
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
            color = MaterialTheme.colors.onSecondary
          )
        },
        trailing = {
          Switch(
            checked = checked,
            onCheckedChange = { checked = !checked },
          )
        }
      )
    }
  }
}
