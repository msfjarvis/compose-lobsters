package dev.msfjarvis.lobsters.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.msfjarvis.lobsters.common.R

private fun stringEnumMapper(stringEnum: Strings): Int {
  return when (stringEnum) {
    Strings.AddToSavedPosts -> R.string.add_to_saved_posts
    Strings.AppName -> R.string.app_name
    Strings.AvatarContentDescription -> R.string.avatar_content_description
    Strings.HottestPosts -> R.string.hottest_posts
    Strings.Loading -> R.string.loading
    Strings.NoSavedPost -> R.string.no_saved_posts
    Strings.OpenComments -> R.string.open_comments
    Strings.RefreshPostsContentDescription -> R.string.refresh_posts_content_description
    Strings.RemoveFromSavedPosts -> R.string.remove_from_saved_posts
    Strings.SubmittedBy -> R.string.submitted_by
  }
}

@Composable
actual fun stringValue(enum: Strings): String {
  return stringResource(stringEnumMapper(enum))
}

/**
 * Workaround for https://youtrack.jetbrains.com/issue/KT-44499
 *
 */
@Composable
actual fun stringValue(enum: Strings, arg1: Any): String {
  return stringResource(stringEnumMapper(enum), arg1)
}

@Composable
actual fun stringValue(enum: Strings, arg1: Any, arg2: Any): String {
  return stringResource(stringEnumMapper(enum), arg1, arg2)
}

@Composable
actual fun stringValue(enum: Strings, arg1: Any, arg2: Any, arg3: Any): String {
  return stringResource(stringEnumMapper(enum), arg1, arg2, arg3)
}
