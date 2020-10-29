package dev.msfjarvis.lobsters.urllauncher

import androidx.compose.runtime.ambientOf

val UrlLauncherAmbient = ambientOf<UrlLauncher> { error("Needs to be provided") }
