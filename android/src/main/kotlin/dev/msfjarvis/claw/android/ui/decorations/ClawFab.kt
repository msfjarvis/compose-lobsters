package dev.msfjarvis.claw.android.ui.decorations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import dev.msfjarvis.claw.android.R
import dev.msfjarvis.claw.android.ui.slideInAnimation
import dev.msfjarvis.claw.android.ui.slideOutAnimation
import kotlinx.coroutines.launch

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
    enter = slideInAnimation(),
    exit = slideOutAnimation(),
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
