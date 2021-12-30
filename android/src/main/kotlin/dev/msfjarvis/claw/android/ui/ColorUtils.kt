package dev.msfjarvis.claw.android.ui

import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import dev.msfjarvis.claw.common.theme.DarkThemeColors
import dev.msfjarvis.claw.common.theme.LightThemeColors

@Composable
fun decideColorScheme(context: Context): ColorScheme {
  val isDarkTheme = isSystemInDarkTheme()
  return if (Build.VERSION.SDK_INT >= 31) {
    if (isDarkTheme) {
      dynamicDarkColorScheme(context)
    } else {
      dynamicLightColorScheme(context)
    }
  } else {
    if (isDarkTheme) {
      DarkThemeColors
    } else {
      LightThemeColors
    }
  }
}
