package dev.msfjarvis.todo.ui

import androidx.compose.animation.animate
import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.msfjarvis.todo.data.model.TodoItem

@Composable
fun ListContent(
  innerPadding: PaddingValues,
  items: List<TodoItem>,
  onSwipe: (TodoItem) -> Unit,
  onClick: (TodoItem) -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyColumnFor(
    modifier = modifier.padding(innerPadding),
    items = items,
  ) { item ->
    AnimatedSwipeDismiss(
      item = item,
      background = { isDismissed ->
        Box(
          modifier = Modifier.background(shape = RoundedCornerShape(8.dp), color = Color.Red)
            .fillMaxSize(),
          paddingStart = 20.dp,
          paddingEnd = 20.dp,
          gravity = ContentGravity.CenterEnd
        ) {
          val alpha = animate(if (isDismissed) 0f else 1f)
          Icon(Icons.Filled.Delete, tint = Color.White.copy(alpha = alpha))
        }
      },
      content = { TodoRowItem(item, onClick) },
      onDismiss = { onSwipe(it) },
      directions = setOf(DismissDirection.EndToStart, DismissDirection.StartToEnd),
    )
  }
}
