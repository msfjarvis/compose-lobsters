package dev.msfjarvis.lobsters.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.msfjarvis.lobsters.common.R

private fun stringEnumMapper(stringEnum: Strings): Int {
  return when (stringEnum) {
    Strings.AddToSavedPosts -> R.string.add_to_saved_posts
    Strings.AppName -> R.string.app_name
    Strings.AvatarContentDescription -> R.string.avatar_content_description
    Strings.ChangeSortingOrder -> R.string.change_sorting_order
    Strings.HottestPosts -> R.string.hottest_posts
    Strings.NewestPosts -> R.string.newest_posts
    Strings.NoSavedPost -> R.string.no_saved_posts
    Strings.OpenComments -> R.string.open_comments
    Strings.RefreshPostsContentDescription -> R.string.refresh_posts_content_description
    Strings.RemoveFromSavedPosts -> R.string.remove_from_saved_posts
    Strings.SavedPosts -> R.string.saved_posts
    Strings.SubmittedBy -> R.string.submitted_by
    Strings.Settings -> R.string.settings
    Strings.SettingsBackup -> R.string.settings_backup
    Strings.SettingsBackupDescription -> R.string.settings_backup_desc
    Strings.SettingsRestore -> R.string.settings_restore
    Strings.SettingsRestoreDescription -> R.string.settings_restore_desc
  }
}

@Composable
actual fun Strings.get(): String {
  return stringResource(stringEnumMapper(this))
}

@Composable
actual fun Strings.get(fmt: Any): String {
  return stringResource(stringEnumMapper(this), fmt)
}
