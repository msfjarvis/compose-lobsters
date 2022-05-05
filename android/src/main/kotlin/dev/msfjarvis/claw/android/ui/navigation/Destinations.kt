package dev.msfjarvis.claw.android.ui.navigation

sealed class Destinations {
  object Hottest : Destinations() {
    fun getRoute() = "hottest"
  }

  object Newest : Destinations() {
    fun getRoute() = "newest"
  }

  object Saved : Destinations() {
    fun getRoute() = "saved"
  }

  object Comments : Destinations() {
    fun getRoute(postId: String) = "comments/$postId"
  }

  object User : Destinations() {
    fun getRoute(username: String) = "user/$username"
  }

  companion object {
    val startDestination
      get() = Hottest
  }
}
