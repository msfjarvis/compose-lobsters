package dev.msfjarvis.lobsters.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.msfjarvis.lobsters.R

/**
 * Destinations for navigation within the app.
 */
enum class Destination(
  val route: String,
  @StringRes val labelRes: Int,
  @DrawableRes val badgeRes: Int,
) {
  Hottest("hottest", R.string.hottest_posts, R.drawable.ic_whatshot_24px),
  Saved("saved", R.string.saved_posts, R.drawable.ic_favorite_24px),
  ;

  companion object {
    val startDestination = Hottest
  }
}
