package dev.msfjarvis.lobsters.data.backup

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import dev.msfjarvis.lobsters.data.local.SavedPost
import dev.msfjarvis.lobsters.data.repo.LobstersRepository
import javax.inject.Inject

@OptIn(ExperimentalStdlibApi::class)
class BackupHandler
@Inject
constructor(
  private val repository: LobstersRepository,
  moshi: Moshi,
) {
  private val adapter = moshi.adapter<List<SavedPost>>()

  suspend fun exportSavedPosts(): ByteArray {
    val posts = repository.getAllPostsFromCache()
    return adapter.toJson(posts).toByteArray(Charsets.UTF_8)
  }

  suspend fun importSavedPosts(json: ByteArray) {
    val posts = requireNotNull(adapter.fromJson(json.toString(Charsets.UTF_8)))
    repository.addPosts(posts)
    repository.updateCache()
  }
}
