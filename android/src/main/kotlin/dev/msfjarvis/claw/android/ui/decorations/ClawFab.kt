package dev.msfjarvis.claw.android.ui.decorations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import dev.msfjarvis.claw.android.R
import kotlinx.coroutines.launch

private const val AnimationDuration = 100

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ClawFab(
  isFabVisible: Boolean,
  listState: LazyListState,
  modifier: Modifier = Modifier,
) {
  val coroutineScope = rememberCoroutineScope()
  AnimatedVisibility(
    visible = isFabVisible,
    enter =
      slideInVertically(
        // Enters by sliding up from offset 0 to fullHeight.
        initialOffsetY = { fullHeight -> fullHeight },
        animationSpec = tween(durationMillis = AnimationDuration, easing = LinearOutSlowInEasing),
      ),
    exit =
      slideOutVertically(
        // Exits by sliding up from offset 0 to -fullHeight.
        targetOffsetY = { fullHeight -> fullHeight },
        animationSpec = tween(durationMillis = AnimationDuration, easing = FastOutLinearInEasing),
      ),
    modifier = modifier,
  ) {
    FloatingActionButton(onClick = { coroutineScope.launch { listState.animateScrollToItem(0) } }) {
      Icon(
        painter = painterResource(R.drawable.ic_arrow_upward_24dp),
        contentDescription = null,
      )
    }
  }
}
