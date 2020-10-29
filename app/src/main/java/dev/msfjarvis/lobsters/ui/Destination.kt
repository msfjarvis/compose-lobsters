package dev.msfjarvis.lobsters.ui

import androidx.annotation.StringRes
import dev.msfjarvis.lobsters.R

/**
 * Destinations for navigation within the app.
 */
sealed class Destination(
  val route: String,
  @StringRes val labelRes: Int,
) {
  object Hottest : Destination("hottest", R.string.hottest_posts)
  object Saved : Destination("saved", R.string.saved_posts)
}
