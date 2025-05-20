package dev.msfjarvis.claw.android.injection

import android.app.Activity
import android.content.Context
import com.deliveryhero.whetstone.ForScope
import com.deliveryhero.whetstone.activity.ActivityScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module

@Module
@ContributesTo(ActivityScope::class)
interface ActivityModule {
  @Binds
  @ForScope(ActivityScope::class)
  fun bindContext(activity: Activity): Context
}
