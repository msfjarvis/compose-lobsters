package dev.msfjarvis.lobsters.ui.urllauncher

import java.awt.Desktop
import java.net.URI

class JVMUrlLauncherImpl() : UrlLauncher {
  override fun launch(url: String) {
    val desktop = Desktop.getDesktop()

    if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
      desktop.browse(URI(url))
    }
  }
}
