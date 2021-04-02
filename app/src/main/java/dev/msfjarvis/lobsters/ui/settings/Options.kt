package dev.msfjarvis.lobsters.ui.settings

import android.content.Context
import androidx.activity.compose.registerForActivityResult
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
    registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri ->
      if (uri == null) return@registerForActivityResult
      context.contentResolver.openOutputStream(uri)?.use {
        coroutineScope.launch(Dispatchers.IO) {
          it.write(backupHandler.exportSavedPosts().toByteArray(Charsets.UTF_8))
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
    registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
      if (uri == null) return@registerForActivityResult
      context.contentResolver.openInputStream(uri)?.use {
        coroutineScope.launch(Dispatchers.IO) {
          backupHandler.importSavedPosts(it.readBytes().toString(Charsets.UTF_8))
        }
      }
    }
  SettingsActionItem(
    title = Strings.SettingsRestore.get(),
    description = Strings.SettingsRestoreDescription.get(),
    onClick = { result.launch(JSON_MINE) }
  )
}
