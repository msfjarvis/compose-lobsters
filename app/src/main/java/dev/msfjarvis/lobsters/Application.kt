package dev.msfjarvis.lobsters

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import saschpe.android.customtabs.CustomTabsActivityLifecycleCallbacks

@HiltAndroidApp
class Application : Application() {
  override fun onCreate() {
    super.onCreate()
    registerActivityLifecycleCallbacks(CustomTabsActivityLifecycleCallbacks())
  }
}
