package dev.msfjarvis.lobsters.ui.navigation

import androidx.annotation.DrawableRes
import dev.msfjarvis.lobsters.R
import dev.msfjarvis.lobsters.utils.Strings

/** Destinations for navigation within the app. */
enum class Destination(
  val route: String,
  val labelRes: Strings,
  @DrawableRes val badgeRes: Int,
  val bottombar: Boolean = true,
) {
  Hottest("hottest", Strings.HottestPosts, R.drawable.ic_whatshot_24px),
  Newest("newest", Strings.NewestPosts, R.drawable.ic_schedule_black_24dp),
  Saved("saved", Strings.SavedPosts, R.drawable.ic_favorite_24px),
  Comments("comments/{postId}", Strings.OpenComments, -1, false),
  ;

  companion object {
    val startDestination = Hottest

    fun getDestinationFromRoute(route: String): Destination {
      return values().firstOrNull { it.route == route } ?: error("Incorrect route passed")
    }
  }
}
