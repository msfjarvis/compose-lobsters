package dev.msfjarvis.claw.android.ui.decorations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.navigation.NavController
import dev.msfjarvis.claw.android.ui.navigation.Destinations
import dev.msfjarvis.claw.android.ui.slideInAnimation
import dev.msfjarvis.claw.android.ui.slideOutAnimation

@Composable
fun ClawNavigationBar(
  navController: NavController,
  items: List<NavigationItem>,
  isVisible: Boolean,
  modifier: Modifier = Modifier,
) {
  AnimatedVisibility(
    visible = isVisible,
    enter = slideInAnimation(),
    exit = slideOutAnimation(),
    modifier = Modifier,
  ) {
    NavigationBar(modifier = modifier) {
      items.forEach { navItem ->
        val isCurrentDestination = navController.currentDestination?.route == navItem.route
        NavigationBarItem(
          icon = {
            Crossfade(isCurrentDestination) {
              Icon(
                painter = if (it) navItem.selectedIcon else navItem.icon,
                contentDescription = navItem.label.uppercase()
              )
            }
          },
          label = { Text(text = navItem.label) },
          selected = isCurrentDestination,
          onClick = {
            if (isCurrentDestination) {
              navItem.listStateResetCallback()
              return@NavigationBarItem
            }
            navController.popBackStack(navController.graph.startDestinationRoute!!, false)
            if (navItem.route != Destinations.startDestination.getRoute()) {
              navController.navigate(navItem.route)
            }
          }
        )
      }
    }
  }
}

class NavigationItem(
  val label: String,
  val route: String,
  val icon: Painter,
  val selectedIcon: Painter,
  val listStateResetCallback: () -> Unit,
)
