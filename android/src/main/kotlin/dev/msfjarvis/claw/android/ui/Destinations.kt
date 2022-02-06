package dev.msfjarvis.claw.android.ui

sealed class Destinations(internal val route: String) {
  object Hottest : Destinations("hottest") {
    fun getRoute() = route
  }

  object Saved : Destinations("saved") {
    fun getRoute() = route
  }

  object Comments : Destinations("comments/%s") {
    fun getRoute(postId: String) = route.format(postId)
  }
}
