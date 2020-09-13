package dev.msfjarvis.todo.urllauncher

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.util.Patterns

class UrlLauncherImpl(private val context: Context) : UrlLauncher {
  override fun launch(url: String) {
    if (!Patterns.WEB_URL.matcher(url).matches()) return
    context.startActivity(Intent(ACTION_VIEW).apply { data = Uri.parse(url) })
  }
}
