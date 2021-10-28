package dev.msfjarvis.claw.common.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

expect val manropeFontFamily: FontFamily

val titleColor = Color(0xFF7395D9)

val lightColors =
  lightColors(
    primary = Color.White,
    secondary = Color(0xFF6C0000),
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.DarkGray,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
  )

val darkColors =
  darkColors(
    primary = Color.White,
    secondary = Color(0xFFD2362D),
    background = Color.Black,
    surface = Color.Black,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
  )

@Composable
fun LobstersTheme(
  darkTheme: Boolean,
  providedValues: Array<ProvidedValue<*>> = emptyArray(),
  children: @Composable () -> Unit
) {
  CompositionLocalProvider(*providedValues) {
    MaterialTheme(
      colors = if (darkTheme) darkColors else lightColors,
      typography =
        Typography(
          defaultFontFamily = manropeFontFamily,
        ),
      content = children,
    )
  }
}
