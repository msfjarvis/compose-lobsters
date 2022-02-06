package dev.msfjarvis.claw.android.ui

import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.navigation.NavController
import dev.msfjarvis.claw.android.ui.navigation.Destinations
import dev.msfjarvis.claw.android.viewmodel.ClawViewModel
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.theme.DarkThemeColors
import dev.msfjarvis.claw.common.theme.LightThemeColors
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import dev.msfjarvis.claw.database.local.SavedPost

// The destination needs to be tracked like this rather than used directly since
// `NavController#currentDestination` is not a Composable state.
@Composable
fun currentNavigationDestination(navController: NavController): State<String?> {
  val currentDestination = remember { mutableStateOf<String?>(null) }
  navController.addOnDestinationChangedListener { _, destination, _ ->
    currentDestination.value = destination.route
  }
  return currentDestination
}

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

@Composable
fun rememberPostActions(
  urlLauncher: UrlLauncher,
  navController: NavController,
  viewModel: ClawViewModel,
): PostActions {
  return remember {
    object : PostActions {
      override fun viewPost(postUrl: String, commentsUrl: String) {
        urlLauncher.openUri(postUrl.ifEmpty { commentsUrl })
      }

      override fun viewComments(postId: String) {
        navController.navigate(Destinations.Comments.getRoute(postId))
      }

      override fun viewCommentsPage(commentsUrl: String) {
        urlLauncher.openUri(commentsUrl)
      }

      override fun toggleSave(post: SavedPost) {
        viewModel.toggleSave(post)
      }
    }
  }
}

private const val ScrollDelta = 50

@Composable
fun rememberNestedScrollConnection(setVisibility: (Boolean) -> Unit): NestedScrollConnection {
  return remember {
    object : NestedScrollConnection {
      override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.y

        if (delta > ScrollDelta) {
          setVisibility(true)
        } else if (delta < -ScrollDelta) {
          setVisibility(false)
        }

        // We didn't consume any offset here so return Offset.Zero
        return Offset.Zero
      }
    }
  }
}
