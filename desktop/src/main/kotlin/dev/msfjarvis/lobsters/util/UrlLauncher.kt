package dev.msfjarvis.lobsters.util

import java.awt.Desktop
import java.net.URI

object UrlLauncher {
  fun launch(url: String) {
    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
      Desktop.getDesktop().browse(URI(url))
    }
  }
}
