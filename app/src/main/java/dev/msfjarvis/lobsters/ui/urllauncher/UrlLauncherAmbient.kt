package dev.msfjarvis.lobsters.ui.urllauncher

import androidx.compose.runtime.staticAmbientOf

val UrlLauncherAmbient = staticAmbientOf<UrlLauncher> { error("Needs to be provided") }
