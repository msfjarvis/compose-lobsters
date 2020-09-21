package dev.msfjarvis.lobsters.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val lightColors = lightColors(
  primary = Color.White,
  secondary = Color(0xFF3700B3),
  background = Color.White,
  surface = Color.White,
  onPrimary = Color.Black,
  onSecondary = Color.White,
  onBackground = Color.Black,
  onSurface = Color.Black,
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

@Composable
fun TodoTheme(children: @Composable () -> Unit) {
  MaterialTheme(
    colors = if (isSystemInDarkTheme()) darkColors else lightColors,
    content = children,
  )
}
