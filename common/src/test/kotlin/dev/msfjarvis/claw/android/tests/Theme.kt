package dev.msfjarvis.claw.android.tests

import androidx.compose.material3.ColorScheme
import dev.msfjarvis.claw.common.theme.DarkThemeColors
import dev.msfjarvis.claw.common.theme.LightThemeColors

enum class Theme(val colors: ColorScheme) {
  Light(LightThemeColors),
  Dark(DarkThemeColors),
}
