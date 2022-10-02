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
class public_black_24dp : Painter() {
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
        moveTo(12.0f, 2.0f)
        cubicTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
        cubicTo(2.0f, 17.52f, 6.48f, 22.0f, 12.0f, 22.0f)
        cubicTo(17.52f, 22.0f, 22.0f, 17.52f, 22.0f, 12.0f)
        cubicTo(22.0f, 6.4799995f, 17.52f, 2.0f, 12.0f, 2.0f)
        close()
        moveTo(4.0f, 12.0f)
        cubicTo(4.0f, 11.39f, 4.08f, 10.79f, 4.21f, 10.22f)
        lineTo(8.99f, 15.0f)
        lineTo(8.99f, 16.0f)
        cubicTo(8.99f, 17.1f, 9.889999f, 18.0f, 10.99f, 18.0f)
        lineTo(10.99f, 19.93f)
        cubicTo(7.06f, 19.43f, 4.0f, 16.07f, 4.0f, 12.0f)
        close()
        moveTo(17.89f, 17.4f)
        cubicTo(17.63f, 16.59f, 16.89f, 16.0f, 15.99f, 16.0f)
        lineTo(14.99f, 16.0f)
        lineTo(14.99f, 13.0f)
        cubicTo(14.99f, 12.45f, 14.54f, 12.0f, 13.99f, 12.0f)
        lineTo(7.99f, 12.0f)
        lineTo(7.99f, 10.0f)
        lineTo(9.99f, 10.0f)
        cubicTo(10.54f, 10.0f, 10.99f, 9.55f, 10.99f, 9.0f)
        lineTo(10.99f, 7.0f)
        lineTo(12.99f, 7.0f)
        cubicTo(14.09f, 7.0f, 14.99f, 6.1f, 14.99f, 5.0f)
        lineTo(14.99f, 4.59f)
        cubicTo(17.92f, 5.77f, 20.0f, 8.65f, 20.0f, 12.0f)
        cubicTo(20.0f, 14.08f, 19.19f, 15.98f, 17.89f, 17.4f)
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
      return 2.0
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
      return 20.0
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
