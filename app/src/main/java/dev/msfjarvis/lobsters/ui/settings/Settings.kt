package dev.msfjarvis.lobsters.ui.settings

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.msfjarvis.lobsters.data.backup.BackupHandler
import dev.msfjarvis.lobsters.utils.Strings
import dev.msfjarvis.lobsters.utils.get
import kotlinx.coroutines.CoroutineScope

@Composable
fun LobstersSettings(
  backupHandler: BackupHandler,
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  Scaffold(
    topBar = { SettingsTopBar(context) },
    content = { SettingsBody(context, backupHandler, scope) },
  )
}

@Composable
fun SettingsTopBar(
  context: Context,
) {
  TopAppBar(
    title = { Text(Strings.Settings.get()) },
    navigationIcon = {
      Icon(
        Icons.Default.ArrowBack,
        contentDescription = Strings.Settings.get(),
        modifier =
          Modifier.padding(start = 16.dp).clickable { (context as ComponentActivity).finish() },
      )
    },
  )
}

@Composable
fun SettingsBody(
  context: Context,
  backupHandler: BackupHandler,
  scope: CoroutineScope,
) {
  LazyColumn {
    item {
      BackupOption(
        context,
        backupHandler,
        scope,
      )
    }
    item {
      RestoreOption(
        context,
        backupHandler,
        scope,
      )
    }
  }
}

@Composable
fun SettingsActionItem(
  title: String,
  description: String? = null,
  singleLineDescription: Boolean = true,
  icon: ImageVector? = null,
  onClick: (() -> Unit)? = null,
) {
  ListItem(
    text = { Text(title) },
    secondaryText = { description?.let { Text(it) } },
    icon = { icon?.let { Icon(icon, null, Modifier.height(32.dp)) } },
    singleLineSecondaryText = singleLineDescription,
    modifier = Modifier.clickable { onClick?.invoke() },
  )
}
