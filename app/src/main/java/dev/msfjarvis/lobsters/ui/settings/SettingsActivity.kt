package dev.msfjarvis.lobsters.ui.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import dev.msfjarvis.lobsters.data.backup.BackupHandler
import dev.msfjarvis.lobsters.ui.theme.LobstersTheme
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : ComponentActivity() {

  @Inject lateinit var backupHandler: BackupHandler

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent { LobstersTheme { LobstersSettings(backupHandler) } }
  }
}
