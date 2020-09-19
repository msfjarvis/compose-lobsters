package dev.msfjarvis.todo.ui

import androidx.compose.animation.animate
import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.Icon
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun <T> ListContent(
  innerPadding: PaddingValues,
  items: List<T>,
  onSwipe: (T) -> Unit,
  onClick: (T) -> Unit,
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
          modifier = Modifier.fillMaxSize(),
          backgroundColor = Color.Red,
          paddingStart = 20.dp,
          paddingEnd = 20.dp,
          gravity = ContentGravity.CenterEnd
        ) {
          val alpha = animate(if (isDismissed) 0f else 1f)
          Icon(Icons.Filled.Delete, tint = Color.White.copy(alpha = alpha))
        }
      },
      content = { /* your item cell (feed your on click here) */ },
      onDismiss = { onSwipe(it) }
    )
  }
}
