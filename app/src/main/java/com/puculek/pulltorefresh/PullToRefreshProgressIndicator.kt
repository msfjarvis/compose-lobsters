package com.puculek.pulltorefresh

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


private val CircularIndicatorDiameter = 40.dp
private const val strokeWidthPx = 2.5f


@Composable
internal fun PullToRefreshProgressIndicator(
  modifier: Modifier = Modifier,
  progressColor: Color,
  backgroundColor: Color,
  progress: Float? = null
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.Center
  ) {
    Card(
      modifier = Modifier
        .width(CircularIndicatorDiameter)
        .height(CircularIndicatorDiameter),
      shape = CircleShape,
      elevation = 6.dp,
      backgroundColor = backgroundColor,
    ) {
      val padding = Modifier.padding(8.dp)
      val strokeWidth = with(LocalDensity.current) {
        (strokeWidthPx * this.density).toDp()
      }

      if (progress == null) {
        CircularProgressIndicator(
          modifier = padding,
          color = progressColor,
          strokeWidth = strokeWidth
        )
      } else {
        ProgressIndicatorWithArrow(
          modifier = padding,
          progress = progress,
          color = progressColor,
          strokeWidth = strokeWidth
        )
      }
    }
  }
}


@Composable
fun ProgressIndicatorWithArrow(
  progress: Float,
  modifier: Modifier = Modifier,
  color: Color = MaterialTheme.colors.primary,
  strokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth,
) {

  val strokeWidthPx = with(LocalDensity.current) {
    strokeWidth.toPx()
  }
  val arrowWidth = 2.5f * strokeWidthPx * (0.5f + progress * 0.5f)
  val stroke = Stroke(width = strokeWidthPx, cap = StrokeCap.Butt)
  val diameterOffset = stroke.width / 2

  val arrowPath = Path().apply {
    moveTo(0f, -arrowWidth)
    lineTo(arrowWidth, 0f)
    lineTo(0f, arrowWidth)
    close()
  }
  Box(modifier = modifier) {
    Canvas(modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight(), onDraw = {
      val arcDimen = size.width - 2 * diameterOffset
      withTransform({
        translate(top = strokeWidthPx / 2, left = size.width / 2)
        rotate(
          degrees = progress * 360,
          pivot = Offset(x = 0f, y = size.height / 2 - diameterOffset)
        )
      }) {
        drawPath(
          path = arrowPath,
          color = color
        )
      }

      drawArc(
        color = color,
        startAngle = -90f,
        sweepAngle = 360 * progress,
        useCenter = false,
        topLeft = Offset(diameterOffset, diameterOffset),
        size = Size(arcDimen, arcDimen),
        style = stroke
      )
    })
  }

}
