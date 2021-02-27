package dev.msfjarvis.lobsters.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import dev.msfjarvis.lobsters.ui.theme.darkColors
import dev.msfjarvis.lobsters.ui.theme.lightColors

@Composable
fun LightTestTheme(children: @Composable () -> Unit) {
  MaterialTheme(
    colors = lightColors,
    content = children,
  )
}

@Composable
fun DarkTestTheme(children: @Composable () -> Unit) {
  MaterialTheme(
    colors = darkColors,
    content = children,
  )
}
