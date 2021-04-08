package dev.msfjarvis.lobsters.ui.settings

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import dev.msfjarvis.lobsters.data.backup.BackupHandler
import dev.msfjarvis.lobsters.utils.Strings
import dev.msfjarvis.lobsters.utils.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val JSON_MINE = "application/json"

@Composable
fun BackupOption(
  context: Context,
  backupHandler: BackupHandler,
  coroutineScope: CoroutineScope,
) {
  val result =
    rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument()) { uri ->
      if (uri == null) return@rememberLauncherForActivityResult
      context.contentResolver.openOutputStream(uri)?.let {
        coroutineScope.launch(Dispatchers.IO) {
          it.write(backupHandler.exportSavedPosts())
          it.close()
        }
      }
    }
  SettingsActionItem(
    Strings.SettingsBackup.get(),
    Strings.SettingsBackupDescription.get(),
    onClick = { result.launch("Claw-export.json") }
  )
}

@Composable
fun RestoreOption(
  context: Context,
  backupHandler: BackupHandler,
  coroutineScope: CoroutineScope,
) {
  val result =
    rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
      if (uri == null) return@rememberLauncherForActivityResult
      context.contentResolver.openInputStream(uri)?.let {
        coroutineScope.launch(Dispatchers.IO) {
          backupHandler.importSavedPosts(it.readBytes())
          it.close()
        }
      }
    }
  SettingsActionItem(
    title = Strings.SettingsRestore.get(),
    description = Strings.SettingsRestoreDescription.get(),
    onClick = { result.launch(JSON_MINE) }
  )
}
