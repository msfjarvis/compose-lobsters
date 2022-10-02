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
class new_releases_black_24dp : Painter() {
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
        moveTo(23.0f, 12.0f)
        lineTo(20.56f, 9.22f)
        lineTo(20.9f, 5.54f)
        lineTo(17.289999f, 4.72f)
        lineTo(15.399999f, 1.5399997f)
        lineTo(12.0f, 3.0f)
        lineTo(8.6f, 1.54f)
        lineTo(6.71f, 4.72f)
        lineTo(3.1000001f, 5.5299997f)
        lineTo(3.44f, 9.21f)
        lineTo(1.0f, 12.0f)
        lineTo(3.44f, 14.78f)
        lineTo(3.1000001f, 18.47f)
        lineTo(6.71f, 19.289999f)
        lineTo(8.6f, 22.47f)
        lineTo(12.0f, 21.0f)
        lineTo(15.4f, 22.46f)
        lineTo(17.289999f, 19.279999f)
        lineTo(20.9f, 18.46f)
        lineTo(20.56f, 14.779999f)
        lineTo(23.0f, 12.0f)
        close()
        moveTo(18.49f, 14.11f)
        lineTo(18.75f, 16.9f)
        lineTo(16.01f, 17.52f)
        lineTo(14.58f, 19.93f)
        lineTo(12.0f, 18.82f)
        lineTo(9.42f, 19.93f)
        lineTo(7.9900002f, 17.52f)
        lineTo(5.25f, 16.9f)
        lineTo(5.51f, 14.099999f)
        lineTo(3.66f, 12.0f)
        lineTo(5.51f, 9.88f)
        lineTo(5.25f, 7.1000004f)
        lineTo(7.99f, 6.4900002f)
        lineTo(9.42f, 4.08f)
        lineTo(12.0f, 5.18f)
        lineTo(14.58f, 4.0699997f)
        lineTo(16.01f, 6.4799995f)
        lineTo(18.75f, 7.0999994f)
        lineTo(18.49f, 9.889999f)
        lineTo(20.34f, 12.0f)
        lineTo(18.49f, 14.11f)
        close()
        moveTo(11.0f, 15.0f)
        lineTo(13.0f, 15.0f)
        lineTo(13.0f, 17.0f)
        lineTo(11.0f, 17.0f)
        close()
        moveTo(11.0f, 7.0f)
        lineTo(13.0f, 7.0f)
        lineTo(13.0f, 13.0f)
        lineTo(11.0f, 13.0f)
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
      return 1.0
    }

    /**
     * Returns the Y of the bounding box of the original SVG image.
     *
     * @return The Y of the bounding box of the original SVG image.
     */
    fun getOrigY(): Double {
      return 1.5399997234344482
    }

    /**
     * Returns the width of the bounding box of the original SVG image.
     *
     * @return The width of the bounding box of the original SVG image.
     */
    fun getOrigWidth(): Double {
      return 22.0
    }

    /**
     * Returns the height of the bounding box of the original SVG image.
     *
     * @return The height of the bounding box of the original SVG image.
     */
    fun getOrigHeight(): Double {
      return 20.93000030517578
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
