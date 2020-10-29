package dev.msfjarvis.lobsters.ui

/**
 * Destinations for navigation within the app.
 */
sealed class Destination(
  val route: String,
  val label: String,
) {
  object Hottest : Destination("hottest", "Hottest")
  object Saved : Destination("saved", "Saved")
}
