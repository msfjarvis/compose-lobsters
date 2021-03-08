package dev.msfjarvis.lobsters.ui.posts

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LoadingLobstersItem() {
  val infiniteTransition = rememberInfiniteTransition()
  val alpha by infiniteTransition.animateFloat(
    initialValue = 0.2f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = keyframes {
        durationMillis = 1000
        0.7f at 500
      },
      repeatMode = RepeatMode.Reverse
    )
  )
  val color = Color.LightGray.copy(alpha = alpha)
  Surface(
    modifier = Modifier.height(70.dp),
  ) {
    Row(
      modifier = Modifier.padding(start = 12.dp, end = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column(
        verticalArrangement = Arrangement.SpaceEvenly,
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(12.dp)
            .background(color)
            .padding(8.dp),
        )
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceEvenly,
          modifier = Modifier
            .absoluteOffset(y = 12.dp),
        ) {
          Box(
            modifier = Modifier
              .requiredSize(30.dp)
              .background(color = color, shape = CircleShape),
          )
          Box(
            modifier = Modifier
              .requiredHeight(12.dp)
              .requiredWidth(40.dp)
              .absoluteOffset(x = 12.dp)
              .background(color),
          )
        }
      }
    }
  }
}

@Preview
@Composable
fun ShimmerListPreview() {
  LazyColumn {
    items(10) {
      LoadingLobstersItem()
    }
  }
}
