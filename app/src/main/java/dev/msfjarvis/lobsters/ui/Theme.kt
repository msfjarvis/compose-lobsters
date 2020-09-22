package dev.msfjarvis.lobsters.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val lightColors = lightColors(
  primary = Color.White,
  secondary = Color(0xFF6C0000),
  background = Color.White,
  surface = Color.White,
  onPrimary = Color.DarkGray,
  onSecondary = Color.White,
  onBackground = Color.Black,
  onSurface = Color.Black,
)

@Composable
fun LobstersTheme(children: @Composable () -> Unit) {
  MaterialTheme(
    colors = lightColors,
    content = children,
  )
}
