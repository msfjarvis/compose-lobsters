package dev.msfjarvis.todo.ui

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ripple.RippleIndication
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.msfjarvis.todo.data.model.TodoItem

@Composable
fun LazyItemScope.TodoRowItem(item: TodoItem, onLongClick: () -> Unit) {
  Row(
    modifier = Modifier.padding(vertical = 8.dp)
      .fillParentMaxWidth()
      .clickable(
        onClick = {},
        onLongClick = onLongClick,
        indication = RippleIndication()
      ),
  ) {
    Card(
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier.fillParentMaxWidth(),
    ) {
      Text(
        text = item.title,
        style = TextStyle(
          fontSize = 20.sp,
          textAlign = TextAlign.Center
        ),
        modifier = Modifier.padding(16.dp),
      )
    }
  }
}
