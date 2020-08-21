package dev.msfjarvis.todo.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.sp

private val lightColors = lightColors(
  primary = Color(0xFF6200EE),
  primaryVariant = Color(0xFF3700B3),
  onPrimary = Color(0xFFFFFFFF),
  secondary = Color(0xFF03DAC5),
  secondaryVariant = Color(0xFF0000FF),
  onSecondary = Color(0xFF000000),
  background = Color(0xFFFFFFFF),
  onBackground = Color(0xFF000000),
  surface = Color(0xFFFFFFFF),
  onSurface = Color(0xFF000000),
  error = Color(0xFFB00020),
  onError = Color(0xFFFFFFFF)
)

private val darkColors = darkColors()

private val typography = Typography(
  body1 = TextStyle(
    fontFamily = FontFamily.Serif,
    fontWeight = FontWeight.Normal,
    fontSize = 20.sp,
    textIndent = TextIndent(firstLine = 16.sp),
    textAlign = TextAlign.Justify
  )
)

@Composable
fun TodoTheme(children: @Composable () -> Unit) {
  MaterialTheme(
    colors = if (isSystemInDarkTheme()) darkColors else lightColors,
    content = children,
    typography = typography,
  )
}
