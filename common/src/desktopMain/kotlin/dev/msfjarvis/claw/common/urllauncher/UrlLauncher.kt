package dev.msfjarvis.claw.common.urllauncher

import androidx.compose.ui.platform.UriHandler
import io.github.aakira.napier.Napier
import java.awt.Desktop
import java.io.IOException
import java.net.URI

class UrlLauncher : UriHandler {
  override fun openUri(uri: String) {
    if (Desktop.isDesktopSupported()) {
      val desktop = Desktop.getDesktop()
      if (desktop.isSupported(Desktop.Action.BROWSE)) {
        try {
          desktop.browse(URI(uri))
        } catch (e: IOException) {
          Napier.d(tag = "UrlLauncher") { "Failed to open URL: $uri" }
        }
      }
    }
  }
}
