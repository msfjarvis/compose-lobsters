/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.posts.api

import java.io.InputStream
import java.io.OutputStream

/** Import and export saved posts without exposing the owning feature's implementation. */
interface PostDataTransfer {
  suspend fun importPosts(input: InputStream): Result<Unit>

  suspend fun exportPostsAsJson(output: OutputStream)
}
