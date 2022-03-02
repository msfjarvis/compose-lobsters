package dev.msfjarvis.claw.android.ui.navigation

sealed class Destinations(val route: String) {
  object Hottest : Destinations("hottest")

  object Saved : Destinations("saved")

  object Comments : Destinations("comments/{postId}")

  companion object {
    val startDestination
      get() = Hottest
  }
}
