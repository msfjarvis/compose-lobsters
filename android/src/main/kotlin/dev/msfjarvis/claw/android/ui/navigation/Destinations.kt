package dev.msfjarvis.claw.android.ui.navigation

sealed class Destinations {
  abstract val route: String

  object Hottest : Destinations() {
    override val route = "hottest"
  }

  object Newest : Destinations() {
    override val route = "newest"
  }

  object Saved : Destinations() {
    override val route = "saved"
  }

  object Comments : Destinations() {
    const val placeholder = "{postId}"
    override val route = "comments/$placeholder"
  }

  object User : Destinations() {
    const val placeholder = "{username}"
    override val route = "user/$placeholder"
  }

  companion object {
    val startDestination
      get() = Hottest
  }
}
