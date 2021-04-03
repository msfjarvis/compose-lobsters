package dev.msfjarvis.lobsters.data.backup

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import dev.msfjarvis.lobsters.data.local.SavedPost
import dev.msfjarvis.lobsters.database.LobstersDatabase
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalStdlibApi::class)
class BackupHandler
@Inject
constructor(
  private val database: LobstersDatabase,
  moshi: Moshi,
) {
  private val adapter = moshi.adapter<List<SavedPost>>()

  suspend fun exportSavedPosts(): ByteArray {
    val posts =
      withContext(Dispatchers.IO) { database.savedPostQueries.selectAllPosts().executeAsList() }
    return adapter.toJson(posts).toByteArray(Charsets.UTF_8)
  }

  suspend fun importSavedPosts(json: ByteArray) {
    withContext(Dispatchers.IO) {
      val posts = requireNotNull(adapter.fromJson(json.toString(Charsets.UTF_8)))
      database.transaction { posts.forEach { database.savedPostQueries.insertOrReplacePost(it) } }
    }
  }
}
