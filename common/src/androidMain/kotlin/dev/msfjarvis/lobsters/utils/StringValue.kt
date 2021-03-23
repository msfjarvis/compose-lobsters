package dev.msfjarvis.lobsters.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.msfjarvis.lobsters.common.R

private fun stringEnumMapper(stringEnum: StringEnum): Int {
  return when (stringEnum) {
    StringEnum.AddToSavedPosts -> R.string.add_to_saved_posts
    StringEnum.AppName -> R.string.app_name
    StringEnum.AvatarContentDescription -> R.string.avatar_content_description
    StringEnum.HottestPosts -> R.string.hottest_posts
    StringEnum.Loading -> R.string.loading
    StringEnum.NoSavedPost -> R.string.no_saved_posts
    StringEnum.OpenComments -> R.string.open_comments
    StringEnum.RefreshPostsContentDescription -> R.string.refresh_posts_content_description
    StringEnum.RemoveFromSavedPosts -> R.string.remove_from_saved_posts
    StringEnum.SubmittedBy -> R.string.submitted_by
  }
}

@Composable
actual fun stringValue(enum: StringEnum): String {
  return stringResource(stringEnumMapper(enum))
}

@Composable
actual fun stringValue(enum: StringEnum, arg1: Any): String {
  return stringResource(stringEnumMapper(enum), arg1)
}

@Composable
actual fun stringValue(enum: StringEnum, arg1: Any, arg2: Any): String {
  return stringResource(stringEnumMapper(enum), arg1, arg2)
}

@Composable
actual fun stringValue(enum: StringEnum, arg1: Any, arg2: Any, arg3: Any): String {
  return stringResource(stringEnumMapper(enum), arg1, arg2, arg3)
}