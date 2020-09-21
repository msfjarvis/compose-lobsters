package dev.msfjarvis.lobsters.ui

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ListItem
import androidx.compose.material.ripple.RippleIndication
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.msfjarvis.lobsters.data.model.TodoItem

@Composable
fun LazyItemScope.TodoRowItem(
  item: TodoItem,
  onClick: (TodoItem) -> Unit,
) {
  Card(
    shape = RoundedCornerShape(8.dp),
    modifier = Modifier.fillParentMaxWidth()
      .clickable(
        onClick = { onClick.invoke(item) },
        indication = RippleIndication()
      ),
  ) {
    ListItem(
      modifier = Modifier.padding(vertical = 8.dp)
        .fillParentMaxWidth(),
      text = {
        Text(
          text = item.title,
          style = TextStyle(
            fontSize = 20.sp,
            textAlign = TextAlign.Center
          ),
          modifier = Modifier.padding(16.dp),
        )
      },
    )
  }
}
