/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.paging

import com.slack.eithernet.ApiResult

/** SAM interface to abstract over a remote API that fetches paginated content. */
fun interface RemoteFetcher<T> {
  suspend fun getItemsAtPage(page: Int): ApiResult<List<T>, Unit>
}
