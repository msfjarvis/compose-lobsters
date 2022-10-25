/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress(
  "ClassNaming",
  "FunctionNaming",
  "FunctionOnlyReturningConstant",
  "LongMethod",
  "MagicNumber",
  "WildcardImport",
)

package dev.msfjarvis.claw.common.res.clawicons

import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.painter.Painter
import java.util.*
import kotlin.math.min

/**
 * This class has been automatically generated using <a
 * href="https://github.com/kirill-grouchnikov/aurora">Aurora SVG transcoder</a>.
 */
class whatshot_filled_black_24dp : Painter() {
  @Suppress("UNUSED_VARIABLE") private var shape: Outline? = null
  @Suppress("UNUSED_VARIABLE") private var generalPath: Path? = null
  @Suppress("UNUSED_VARIABLE") private var brush: Brush? = null
  @Suppress("UNUSED_VARIABLE") private var stroke: Stroke? = null
  @Suppress("UNUSED_VARIABLE") private var clip: Shape? = null
  private var alpha = 1.0f
  private var blendMode = DrawScope.DefaultBlendMode
  private var alphaStack = mutableListOf(1.0f)
  private var blendModeStack = mutableListOf(DrawScope.DefaultBlendMode)

  private fun _paint0(drawScope: DrawScope) {
    @Suppress("UNUSED_VARIABLE") var shapeText: Outline?
    @Suppress("UNUSED_VARIABLE") var generalPathText: Path? = null
    @Suppress("UNUSED_VARIABLE") var alphaText = 0.0f
    @Suppress("UNUSED_VARIABLE") var blendModeText = DrawScope.DefaultBlendMode
    with(drawScope) {
      //
      alphaStack.add(0, alpha)
      alpha *= 1.0f
      blendModeStack.add(0, BlendMode.SrcOver)
      blendMode = BlendMode.SrcOver
      // _0
      alphaStack.add(0, alpha)
      alpha *= 1.0f
      blendModeStack.add(0, BlendMode.SrcOver)
      blendMode = BlendMode.SrcOver
      // _0_0
      alpha = alphaStack.removeAt(0)
      blendMode = blendModeStack.removeAt(0)
      alphaStack.add(0, alpha)
      alpha *= 1.0f
      blendModeStack.add(0, BlendMode.SrcOver)
      blendMode = BlendMode.SrcOver
      // _0_1
      if (generalPath == null) {
        generalPath = Path()
      } else {
        generalPath!!.reset()
      }
      generalPath?.run {
        moveTo(13.5f, 0.67f)
        cubicTo(13.5f, 0.67f, 14.24f, 3.3200002f, 14.24f, 5.4700003f)
        cubicTo(14.24f, 7.53f, 12.889999f, 9.200001f, 10.83f, 9.200001f)
        cubicTo(8.76f, 9.200001f, 7.2f, 7.5300007f, 7.2f, 5.4700007f)
        lineTo(7.23f, 5.1100006f)
        cubicTo(5.21f, 7.51f, 4.0f, 10.62f, 4.0f, 14.0f)
        cubicTo(4.0f, 18.42f, 7.58f, 22.0f, 12.0f, 22.0f)
        cubicTo(16.42f, 22.0f, 20.0f, 18.42f, 20.0f, 14.0f)
        cubicTo(20.0f, 8.61f, 17.41f, 3.8f, 13.5f, 0.67f)
        close()
        moveTo(11.71f, 19.0f)
        cubicTo(9.93f, 19.0f, 8.49f, 17.6f, 8.49f, 15.86f)
        cubicTo(8.49f, 14.24f, 9.54f, 13.099999f, 11.299999f, 12.74f)
        cubicTo(13.07f, 12.38f, 14.9f, 11.53f, 15.919999f, 10.16f)
        cubicTo(16.31f, 11.45f, 16.509998f, 12.809999f, 16.509998f, 14.2f)
        cubicTo(16.509998f, 16.85f, 14.359999f, 19.0f, 11.709998f, 19.0f)
        close()
      }
      shape = Outline.Generic(generalPath!!)
      brush = SolidColor(Color(0, 0, 0, 255))
      drawOutline(
        outline = shape!!,
        style = Fill,
        brush = brush!!,
        alpha = alpha,
        blendMode = blendMode
      )
      alpha = alphaStack.removeAt(0)
      blendMode = blendModeStack.removeAt(0)
      alpha = alphaStack.removeAt(0)
      blendMode = blendModeStack.removeAt(0)
    }
  }

  private fun innerPaint(drawScope: DrawScope) {
    _paint0(drawScope)

    shape = null
    generalPath = null
    brush = null
    stroke = null
    clip = null
    alpha = 1.0f
  }

  companion object {
    /**
     * Returns the X of the bounding box of the original SVG image.
     *
     * @return The X of the bounding box of the original SVG image.
     */
    fun getOrigX(): Double {
      return 4.0
    }

    /**
     * Returns the Y of the bounding box of the original SVG image.
     *
     * @return The Y of the bounding box of the original SVG image.
     */
    fun getOrigY(): Double {
      return 0.6700000166893005
    }

    /**
     * Returns the width of the bounding box of the original SVG image.
     *
     * @return The width of the bounding box of the original SVG image.
     */
    fun getOrigWidth(): Double {
      return 16.0
    }

    /**
     * Returns the height of the bounding box of the original SVG image.
     *
     * @return The height of the bounding box of the original SVG image.
     */
    fun getOrigHeight(): Double {
      return 21.329999923706055
    }
  }

  override val intrinsicSize: Size
    get() = Size.Unspecified

  override fun DrawScope.onDraw() {
    clipRect {
      // Use the original icon bounding box and the current icon dimension to compute
      // the scaling factor
      val fullOrigWidth = getOrigX() + getOrigWidth()
      val fullOrigHeight = getOrigY() + getOrigHeight()
      val coef1 = size.width / fullOrigWidth
      val coef2 = size.height / fullOrigHeight
      val coef = min(coef1, coef2).toFloat()

      // Use the original icon bounding box and the current icon dimension to compute
      // the offset pivot for the scaling
      var translateX = -getOrigX()
      var translateY = -getOrigY()
      if (coef1 != coef2) {
        if (coef1 < coef2) {
          val extraDy = ((fullOrigWidth - fullOrigHeight) / 2.0f).toFloat()
          translateY += extraDy
        } else {
          val extraDx = ((fullOrigHeight - fullOrigWidth) / 2.0f).toFloat()
          translateX += extraDx
        }
      }
      val translateXDp = translateX.toFloat().toDp().value
      val translateYDp = translateY.toFloat().toDp().value

      // Create a combined scale + translate + clip transform before calling the transcoded painting
      // instructions
      withTransform({
        scale(scaleX = coef, scaleY = coef, pivot = Offset.Zero)
        translate(translateXDp, translateYDp)
        clipRect(
          left = 0.0f,
          top = 0.0f,
          right = fullOrigWidth.toFloat(),
          bottom = fullOrigHeight.toFloat(),
          clipOp = ClipOp.Intersect
        )
      }) {
        innerPaint(this)
      }
    }
  }
}
