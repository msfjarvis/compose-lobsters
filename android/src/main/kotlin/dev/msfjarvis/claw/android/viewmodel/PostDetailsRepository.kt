package dev.msfjarvis.claw.android.viewmodel

import dev.msfjarvis.claw.metadata.MetadataExtractor
import dev.msfjarvis.claw.model.ExtendedPostDetails
import dev.msfjarvis.claw.model.LobstersPostDetails
import javax.inject.Inject

class PostDetailsRepository
@Inject
constructor(
  private val metadataExtractor: MetadataExtractor,
) {
  suspend fun getExtendedDetails(details: LobstersPostDetails): ExtendedPostDetails {
    val metadata = metadataExtractor.getExtractedMetadata(details.url)
    return ExtendedPostDetails(
      title = details.title,
      linkMetadata = metadata,
      description = details.description,
      submitter = details.submitter,
      tags = details.tags,
      comments = details.comments,
      commentsUrl = details.commentsUrl,
    )
  }
}
