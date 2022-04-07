package dev.msfjarvis.claw.common.urllauncher

import androidx.compose.ui.platform.UriHandler
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
          println("Failed to open URL: $uri")
        }
      }
    }
  }
}
