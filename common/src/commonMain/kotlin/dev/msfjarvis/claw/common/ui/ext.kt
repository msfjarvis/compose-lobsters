package dev.msfjarvis.claw.common.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import kotlin.math.ln

/**
 * Returns the [ColorScheme.surface] color with an alpha of the [ColorScheme.primary] color overlaid
 * on top of it. Computes the surface tonal color at different elevation levels e.g. surface1
 * through surface5.
 *
 * Stolen from AndroidX, keep in sync when upgrading Compose. This version is hard-coded to
 * replicate the logic used by the Material3 NavigationBar to determine its surface color.
 * https://github.com/androidx/androidx/blob/74d3510b608c3cc26b9cf9be8d15a6a6c26192c2/compose/material3/material3/src/commonMain/kotlin/androidx/compose/material3/ColorScheme.kt#L453-L466
 */
@Composable
fun ColorScheme.surfaceColorAtNavigationBarElevation(): Color {
  // Absolute tonal elevation + NavigationBarTokens.ContainerElevation
  val elevation = LocalAbsoluteTonalElevation.current + 3.0.dp
  if (elevation == 0.dp) return surface
  val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f
  return primary.copy(alpha = alpha).compositeOver(surface)
}
