/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import dev.msfjarvis.claw.core.injection.IODispatcher
import dev.msfjarvis.claw.model.LinkMetadata
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import me.saket.unfurl.Unfurler

class LinkMetadataRepository
@Inject
constructor(
  private val unfurler: Unfurler,
  @IODispatcher private val dispatcher: CoroutineDispatcher,
) {
  suspend fun getLinkMetadata(url: String): LinkMetadata {
    val result = withContext(dispatcher) { unfurler.unfurl(url) }
    return LinkMetadata(url, result?.favicon?.toString())
  }
}
