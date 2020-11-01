package dev.msfjarvis.lobsters.ui.urllauncher

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

class UrlLauncherImpl(private val context: Context) : UrlLauncher {
  override fun launch(url: String) {
    val customTabsIntent = CustomTabsIntent.Builder()
      .setShareState(CustomTabsIntent.SHARE_STATE_ON)
      .setShowTitle(true)
      .setColorScheme(CustomTabsIntent.COLOR_SCHEME_DARK)
      .build()
    customTabsIntent.launchUrl(context, Uri.parse(url))
  }
}
