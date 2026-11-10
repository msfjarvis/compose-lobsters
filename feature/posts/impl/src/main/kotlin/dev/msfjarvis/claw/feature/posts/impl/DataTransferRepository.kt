/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.posts.impl

import dev.msfjarvis.claw.core.coroutines.IODispatcher
import dev.msfjarvis.claw.feature.posts.api.PostDataTransfer
import dev.msfjarvis.claw.feature.posts.api.SavedPosts
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@Inject
@ContributesBinding(AppScope::class, binding = binding<PostDataTransfer>())
class DataTransferRepository(
  private val savedPostsRepository: SavedPosts,
  @param:IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : PostDataTransfer {
  override suspend fun importPosts(input: InputStream): Result<Unit> {
    return try {
      val serialized = withContext(ioDispatcher) { input.readBytes().decodeToString() }
      savedPostsRepository.importSerializedPosts(serialized)
      Result.success(Unit)
    } catch (t: Throwable) {
      Result.failure(t)
    }
  }

  override suspend fun exportPostsAsJson(output: OutputStream) {
    val serialized = savedPostsRepository.exportSerializedPosts()
    withContext(ioDispatcher) { output.write(serialized.encodeToByteArray()) }
  }
}
