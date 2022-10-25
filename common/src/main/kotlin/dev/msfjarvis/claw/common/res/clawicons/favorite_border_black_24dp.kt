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
class favorite_border_black_24dp : Painter() {
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
        moveTo(16.5f, 3.0f)
        cubicTo(14.76f, 3.0f, 13.09f, 3.81f, 12.0f, 5.09f)
        cubicTo(10.91f, 3.81f, 9.24f, 3.0f, 7.5f, 3.0f)
        cubicTo(4.42f, 3.0f, 2.0f, 5.42f, 2.0f, 8.5f)
        cubicTo(2.0f, 12.28f, 5.4f, 15.360001f, 10.55f, 20.04f)
        lineTo(12.0f, 21.35f)
        lineTo(13.45f, 20.03f)
        cubicTo(18.6f, 15.36f, 22.0f, 12.28f, 22.0f, 8.5f)
        cubicTo(22.0f, 5.42f, 19.58f, 3.0f, 16.5f, 3.0f)
        close()
        moveTo(12.1f, 18.55f)
        lineTo(12.0f, 18.65f)
        lineTo(11.9f, 18.55f)
        cubicTo(7.14f, 14.24f, 4.0f, 11.39f, 4.0f, 8.5f)
        cubicTo(4.0f, 6.5f, 5.5f, 5.0f, 7.5f, 5.0f)
        cubicTo(9.04f, 5.0f, 10.54f, 5.99f, 11.07f, 7.3599997f)
        lineTo(12.94f, 7.3599997f)
        cubicTo(13.46f, 5.99f, 14.96f, 5.0f, 16.5f, 5.0f)
        cubicTo(18.5f, 5.0f, 20.0f, 6.5f, 20.0f, 8.5f)
        cubicTo(20.0f, 11.39f, 16.86f, 14.24f, 12.1f, 18.55f)
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
      return 2.0
    }

    /**
     * Returns the Y of the bounding box of the original SVG image.
     *
     * @return The Y of the bounding box of the original SVG image.
     */
    fun getOrigY(): Double {
      return 3.0
    }

    /**
     * Returns the width of the bounding box of the original SVG image.
     *
     * @return The width of the bounding box of the original SVG image.
     */
    fun getOrigWidth(): Double {
      return 20.0
    }

    /**
     * Returns the height of the bounding box of the original SVG image.
     *
     * @return The height of the bounding box of the original SVG image.
     */
    fun getOrigHeight(): Double {
      return 18.350000381469727
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
