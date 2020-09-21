package dev.msfjarvis.lobsters.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onCommit
import androidx.compose.ui.Modifier

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun <T> AnimatedSwipeDismiss(
  modifier: Modifier = Modifier,
  item: T,
  background: @Composable (isDismissed: Boolean) -> Unit,
  content: @Composable (isDismissed: Boolean) -> Unit,
  directions: Set<DismissDirection> = setOf(DismissDirection.EndToStart),
  enter: EnterTransition = expandVertically(),
  exit: ExitTransition = shrinkVertically(
    animSpec = tween(
      durationMillis = 500,
    )
  ),
  onDismiss: (T) -> Unit
) {
  val dismissState = rememberDismissState()
  val isDismissed = dismissState.isDismissed(DismissDirection.EndToStart)

  onCommit(dismissState.value) {
    if (dismissState.value == DismissValue.DismissedToStart) {
      onDismiss(item)
    }
  }

  AnimatedVisibility(
    modifier = modifier,
    visible = !isDismissed,
    enter = enter,
    exit = exit
  ) {
    SwipeToDismiss(
      modifier = modifier,
      state = dismissState,
      directions = directions,
      background = { background(isDismissed) },
      dismissContent = { content(isDismissed) }
    )
  }
}
