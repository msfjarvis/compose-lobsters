package dev.msfjarvis.lobsters.utils

import androidx.compose.runtime.Composable

private fun stringEnumMapper(stringEnum: StringEnum): String {
  return when (stringEnum) {
    StringEnum.AddToSavedPosts -> "Add to saved posts"
    StringEnum.AppName -> "Claw"
    StringEnum.AvatarContentDescription -> "%1s's avatar"
    StringEnum.HottestPosts -> "Hottest"
    StringEnum.Loading -> "Loading posts…"
    StringEnum.NoSavedPost -> "You don't have any saved posts"
    StringEnum.OpenComments -> "Open comments"
    StringEnum.RefreshPostsContentDescription -> "Refresh posts"
    StringEnum.RemoveFromSavedPosts -> "Remove from saved posts"
    StringEnum.SubmittedBy -> "submitted by %1s"
  }
}

@Composable
actual fun stringValue(enum: StringEnum): String {
  return stringEnumMapper(enum)
}

@Composable
actual fun stringValue(enum: StringEnum, arg1: Any): String {
  return stringEnumMapper(enum).format(arg1)
}

@Composable
actual fun stringValue(enum: StringEnum, arg1: Any, arg2: Any): String {
  return stringEnumMapper(enum).format(arg1, arg2)
}

@Composable
actual fun stringValue(enum: StringEnum, arg1: Any, arg2: Any, arg3: Any): String {
  return stringEnumMapper(enum).format(arg1, arg2, arg3)
}