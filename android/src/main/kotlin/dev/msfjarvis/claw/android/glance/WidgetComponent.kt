package dev.msfjarvis.claw.android.glance

import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import dev.msfjarvis.claw.android.viewmodel.SavedPostsRepository

@ContributesTo(ApplicationScope::class)
interface WidgetComponent {
  fun savedPosts(): SavedPostsRepository
}
