package dev.msfjarvis.lobsters.urllauncher

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

class UrlLauncherImpl(private val context: Context) : UrlLauncher {
  override fun launch(url: String) {
    val customTabsIntent = CustomTabsIntent.Builder()
      .setShareState(CustomTabsIntent.SHARE_STATE_ON)
      .setShowTitle(true)
      .build()
    customTabsIntent.launchUrl(context, Uri.parse(url))
  }
}
