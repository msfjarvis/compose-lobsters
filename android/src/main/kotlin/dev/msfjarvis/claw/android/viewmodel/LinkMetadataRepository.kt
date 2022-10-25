/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import dev.msfjarvis.claw.metadata.MetadataExtractor
import dev.msfjarvis.claw.model.LinkMetadata
import javax.inject.Inject

class LinkMetadataRepository
@Inject
constructor(
  private val metadataExtractor: MetadataExtractor,
) {
  suspend fun getLinkMetadata(url: String): LinkMetadata {
    return metadataExtractor.getExtractedMetadata(url)
  }
}
