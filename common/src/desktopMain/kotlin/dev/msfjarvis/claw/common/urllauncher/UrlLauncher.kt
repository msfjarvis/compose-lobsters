package dev.msfjarvis.claw.common.urllauncher

import java.awt.Desktop
import java.net.URI

actual class UrlLauncher {
  actual fun launch(url: String) {
    if (Desktop.isDesktopSupported()) {
      val desktop = Desktop.getDesktop()
      if (desktop.isSupported(Desktop.Action.BROWSE)) {
        desktop.browse(URI(url))
      }
    }
  }
}
