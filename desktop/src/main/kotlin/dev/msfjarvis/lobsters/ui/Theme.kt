@file:Suppress("UNUSED")
package dev.msfjarvis.lobsters.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val titleColor = Color(0xFF7395D9)

val lightColors = lightColors(
  primary = Color.White,
  secondary = Color(0xFF6C0000),
  background = Color.White,
  surface = Color.White,
  onPrimary = Color.DarkGray,
  onSecondary = Color.White,
  onBackground = Color.White,
  onSurface = Color.White,
)

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
fun LobstersTheme(
  useLightColors: Boolean = true,
  children: @Composable () -> Unit
) {
  MaterialTheme(
    colors = if (useLightColors) lightColors else darkColors,
    content = children,
  )
}
