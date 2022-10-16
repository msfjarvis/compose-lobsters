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
