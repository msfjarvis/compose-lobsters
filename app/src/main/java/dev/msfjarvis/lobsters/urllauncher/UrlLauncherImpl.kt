package dev.msfjarvis.lobsters.urllauncher

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import saschpe.android.customtabs.CustomTabsHelper
import saschpe.android.customtabs.WebViewFallback

class UrlLauncherImpl(private val context: Context) : UrlLauncher {
  override fun launch(url: String) {
    val customTabsIntent = CustomTabsIntent.Builder()
      .addDefaultShareMenuItem()
      .setShowTitle(true)
      .build()
    CustomTabsHelper.addKeepAliveExtra(context, customTabsIntent.intent)
    CustomTabsHelper.openCustomTab(
      context, customTabsIntent,
      Uri.parse(url),
      WebViewFallback()
    )
  }
}
