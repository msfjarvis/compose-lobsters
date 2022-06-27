package dev.msfjarvis.claw.android.ui

import android.content.Context
import android.os.Build
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import dev.msfjarvis.claw.android.ui.navigation.Destinations
import dev.msfjarvis.claw.android.viewmodel.ClawViewModel
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.theme.DarkThemeColors
import dev.msfjarvis.claw.common.theme.LightThemeColors
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import dev.msfjarvis.claw.database.local.SavedPost
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private const val AnimationDuration = 100

fun slideInAnimation(): EnterTransition {
  return slideInVertically(
    // Enters by sliding up from offset 0 to fullHeight.
    initialOffsetY = { fullHeight -> fullHeight },
    animationSpec = tween(durationMillis = AnimationDuration, easing = LinearOutSlowInEasing),
  )
}

fun slideOutAnimation(): ExitTransition {
  return slideOutVertically(
    // Exits by sliding up from offset 0 to -fullHeight.
    targetOffsetY = { fullHeight -> fullHeight },
    animationSpec = tween(durationMillis = AnimationDuration, easing = FastOutLinearInEasing),
  )
}

/**
 * Parses a given [String] into a [LocalDateTime]. This method is only intended to be used for dates
 * in the format returned by the Lobsters API, and is not a general purpose parsing solution.
 */
fun String.toLocalDateTime(): LocalDateTime {
  return Instant.parse(this).toLocalDateTime(TimeZone.currentSystemDefault())
}

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
        // Post links from lobste.rs are of the form $baseUrl/s/$postId/$postTitle
        // Interestingly, lobste.rs does not actually care for the value of $postTitle, and will
        // happily accept both a missing as well as a completely arbitrary $postTitle. We
        // leverage this to create a new URL format which looks like
        // $baseUrl/s/$postId/$postTitle/r, and does not trigger our deeplinks,
        // instead opening in the custom tab as we want it to.
        urlLauncher.openUri(commentsUrl.replaceAfterLast('/', "r"))
      }

      override fun toggleSave(post: SavedPost) {
        viewModel.toggleSave(post)
      }
    }
  }
}
