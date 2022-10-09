package dev.msfjarvis.claw.android.paging

import com.slack.eithernet.ApiResult

/** SAM interface to abstract over a remote API that fetches paginated content. */
fun interface RemoteFetcher<T> {
  suspend fun getItemsAtPage(page: Int): ApiResult<List<T>, Unit>
}
