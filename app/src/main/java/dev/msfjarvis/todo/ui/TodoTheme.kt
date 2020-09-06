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

val lightColors = lightColors(
  primary = Color.White,
  secondary = Color(0xFF3700B3),
  background = Color.White,
  surface = Color.White,
  onPrimary = Color.Black,
  onSecondary = Color.White,
  onBackground = Color.Black,
  onSurface = Color.White,
)
val darkColors = darkColors(
  primary = Color(0xFF121212),
  secondary = Color(0xFFBB86FC),
  background = Color.Black,
  surface = Color(0xFF121212),
  onPrimary = Color.White,
  onSecondary = Color.White,
  onBackground = Color.White,
  onSurface = Color.White,
)

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
