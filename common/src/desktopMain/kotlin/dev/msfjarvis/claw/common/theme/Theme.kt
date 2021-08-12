@file:JvmName("DesktopTheme")

package dev.msfjarvis.claw.common.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font

actual val manropeFontFamily =
  FontFamily(
    Font("font/manrope_bold.ttf", FontWeight.Bold),
    Font("font/manrope_extrabold.ttf", FontWeight.ExtraBold),
    Font("font/manrope_extralight.ttf", FontWeight.ExtraLight),
    Font("font/manrope_light.ttf", FontWeight.Light),
    Font("font/manrope_medium.ttf", FontWeight.Medium),
    Font("font/manrope_regular.ttf", FontWeight.Normal),
    Font("font/manrope_semibold.ttf", FontWeight.SemiBold),
  )
