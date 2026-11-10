/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.widgets.impl

import dev.msfjarvis.claw.feature.tags.api.TagBlocks
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@BindingContainer
@ContributesTo(AppScope::class)
object WidgetTagFilterModule {
  @Provides
  fun providesWidgetTagFilter(tagBlocks: TagBlocks): WidgetTagFilter =
    WidgetTagFilter(tagBlocks::getSavedTagsSnapshot)
}
