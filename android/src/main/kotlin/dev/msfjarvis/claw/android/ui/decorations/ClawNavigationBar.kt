package dev.msfjarvis.claw.android.ui.decorations

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.navigation.NavController

@Composable
fun ClawNavigationBar(
  navController: NavController,
  items: List<NavigationItem>,
  modifier: Modifier = Modifier,
) {
  var selectedIndex by remember { mutableStateOf(0) }

  NavigationBar(modifier = modifier) {
    items.forEachIndexed { index, navItem ->
      NavigationBarItem(
        icon = { Icon(painter = navItem.icon, contentDescription = navItem.label) },
        label = { Text(text = navItem.label) },
        selected = selectedIndex == index,
        onClick = {
          selectedIndex = index
          navController.navigate(navItem.route)
        }
      )
    }
  }
}

class NavigationItem(
  val label: String,
  val route: String,
  val icon: Painter,
)
