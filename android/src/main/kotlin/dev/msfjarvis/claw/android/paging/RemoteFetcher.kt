package dev.msfjarvis.claw.android.paging

/** SAM interface to abstract over a remote API that fetches paginated content. */
fun interface RemoteFetcher<T> {
  suspend fun getItemsAtPage(page: Int): List<T>
}
