package dev.msfjarvis.lobsters.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val darkColors = darkColors(
  primary = Color.White,
  secondary = Color(0xFF6C0000),
  background = Color.Black,
  surface = Color.Black,
  onPrimary = Color.Black,
  onSecondary = Color.White,
  onBackground = Color.White,
  onSurface = Color.White,
)

@Composable
fun LobstersTheme(children: @Composable () -> Unit) {
  MaterialTheme(
    colors = darkColors,
    content = children,
  )
}
