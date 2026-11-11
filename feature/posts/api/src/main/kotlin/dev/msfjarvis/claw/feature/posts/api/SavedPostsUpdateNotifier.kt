/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.posts.api

/** Notifies app integrations after a saved-post refresh has persisted updated posts. */
fun interface SavedPostsUpdateNotifier {
  suspend fun onSavedPostsUpdated()
}
