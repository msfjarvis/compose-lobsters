package dev.msfjarvis.lobsters.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.msfjarvis.lobsters.R

/**
 * Destinations for navigation within the app.
 */
sealed class Destination(
  val route: String,
  @StringRes val labelRes: Int,
  @DrawableRes val badgeRes: Int,
) {
  object Hottest : Destination("hottest", R.string.hottest_posts, R.drawable.ic_whatshot_24px)
  object Saved : Destination("saved", R.string.saved_posts, R.drawable.ic_favorite_24px)
}
