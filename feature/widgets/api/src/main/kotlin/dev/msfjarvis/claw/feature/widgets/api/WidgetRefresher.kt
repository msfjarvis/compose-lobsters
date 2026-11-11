/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.widgets.api

/** Requests a refresh of the saved-post widget after saved posts change. */
fun interface WidgetRefresher {
  suspend fun refreshSavedPosts()
}
